package com.yourname.taskmanager.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.taskmanager.data.Reminder
import com.yourname.taskmanager.ui.viewmodel.ReminderViewModel
import com.yourname.taskmanager.utils.toDateString
import com.yourname.taskmanager.utils.toTimeString
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReminderScreen(
    reminderViewModel: ReminderViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    reminder: Reminder? = null
) {
    var title by remember { mutableStateOf(reminder?.title ?: "") }
    var notes by remember { mutableStateOf(reminder?.notes ?: "") }
    var dueDate by remember { mutableStateOf(reminder?.dueDate ?: System.currentTimeMillis()) }
    var time by remember { mutableStateOf(reminder?.time ?: System.currentTimeMillis()) }
    var repeat by remember { mutableStateOf(reminder?.repeat ?: "Does not repeat") }
    var category by remember { mutableStateOf(reminder?.category ?: "Default") }
    var backgroundColor by remember { mutableStateOf(reminder?.backgroundColor ?: "#FFFFFF") }
    var ringtoneUri by remember { mutableStateOf(reminder?.ringtone?.takeIf { it.isNotBlank() && it != "null" }?.let { Uri.parse(it) }) }

    val context = LocalContext.current

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            dueDate = calendar.timeInMillis
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val calendar = Calendar.getInstance()
            calendar.time = Date(time)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            time = calendar.timeInMillis
        },
        Calendar.getInstance().apply { this.time = Date(time) }.get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().apply { this.time = Date(time) }.get(Calendar.MINUTE),
        false
    )

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }
            ringtoneUri = uri
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (reminder == null) "Add Reminder" else "Edit Reminder") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    if (reminder != null) {
                        IconButton(onClick = {
                            reminderViewModel.deleteReminder(reminder)
                            onNavigateBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Reminder")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val finalRingtoneUri = ringtoneUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                    val newReminder = reminder?.copy(
                        title = title, notes = notes, dueDate = dueDate, time = time, repeat = repeat,
                        category = category, backgroundColor = backgroundColor, ringtone = finalRingtoneUri?.toString()
                    ) ?: Reminder(
                        title = title, notes = notes, dueDate = dueDate, time = time, repeat = repeat,
                        category = category, backgroundColor = backgroundColor, ringtone = finalRingtoneUri?.toString()
                    )
                    reminderViewModel.insertReminder(newReminder)
                    onNavigateBack()
                }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Reminder")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it }, label = { Text("Reminder Name") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notes, onValueChange = { if (it.length <= 300) notes = it }, label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(), maxLines = 5, supportingText = { Text("${notes.length} / 300") }
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { datePickerDialog.show() }, modifier = Modifier.weight(1f)) {
                    Text(text = dueDate.toDateString())
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(onClick = { timePickerDialog.show() }, modifier = Modifier.weight(1f)) {
                    Text(text = time.toTimeString())
                }
            }

            var repeatExpanded by remember { mutableStateOf(false) }
            val repeatOptions = listOf("Does not repeat", "Every day", "Every week", "Every month", "Every year", "Custom")
            ExposedDropdownMenuBox(expanded = repeatExpanded, onExpandedChange = { repeatExpanded = !repeatExpanded }) {
                OutlinedTextField(
                    value = repeat, onValueChange = {}, readOnly = true, label = { Text("Repeat") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = repeatExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = repeatExpanded, onDismissRequest = { repeatExpanded = false }) {
                    repeatOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { repeat = option; repeatExpanded = false })
                    }
                }
            }

            var categoryExpanded by remember { mutableStateOf(false) }
            val categoryOptions = listOf("Default", "Routine", "Important", "Goals", "Career")
            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
                OutlinedTextField(
                    value = category, onValueChange = {}, readOnly = true, label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    categoryOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { category = option; categoryExpanded = false })
                    }
                }
            }

            val colors = listOf("#FFFFFF", "#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#C5CAE9", "#BBDEFB", "#B3E5FC", "#B2EBF2", "#B2DFDB", "#C8E6C9", "#DCEDC8", "#F0F4C3", "#FFF9C4", "#FFECB3", "#FFE0B2", "#FFCCBC")
            Text("Background Color", style = MaterialTheme.typography.bodyLarge)
            LazyRow {
                items(colors) { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp).padding(4.dp).clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(color)))
                            .clickable { backgroundColor = color }
                            .border(width = 2.dp, color = if (backgroundColor == color) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, shape = CircleShape)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().clickable {
                    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                    ringtonePickerLauncher.launch(intent)
                }.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ringtone", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = ringtoneUri?.let { uri -> RingtoneManager.getRingtone(context, uri)?.getTitle(context) } ?: "Default",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
