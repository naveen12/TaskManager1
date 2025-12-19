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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yourname.taskmanager.data.Priority
import com.yourname.taskmanager.data.Task
import com.yourname.taskmanager.ui.viewmodel.TaskViewModel
import com.yourname.taskmanager.utils.toColor
import com.yourname.taskmanager.utils.toDateString
import com.yourname.taskmanager.utils.toDisplayString
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    task: Task,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf(task.title) }
    var notes by remember { mutableStateOf(task.notes) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    var duration by remember { mutableStateOf(task.duration) }
    var repeat by remember { mutableStateOf(task.repeat) }
    var priority by remember { mutableStateOf(task.priority) }
    var backgroundColor by remember { mutableStateOf(task.backgroundColor) }
    var ringtoneUri by remember { mutableStateOf(task.ringtone?.takeIf { it.isNotBlank() && it != "null" }?.let { Uri.parse(it) }) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dueDate
            calendar.set(year, month, dayOfMonth)
            dueDate = calendar.timeInMillis
        },
        Calendar.getInstance().apply { timeInMillis = dueDate }.get(Calendar.YEAR),
        Calendar.getInstance().apply { timeInMillis = dueDate }.get(Calendar.MONTH),
        Calendar.getInstance().apply { timeInMillis = dueDate }.get(Calendar.DAY_OF_MONTH)
    )

    val durationPickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            duration = (hour * 60 * 60 * 1000 + minute * 60 * 1000).toLong()
        },
        (duration / (60 * 60 * 1000)).toInt(),
        (duration % (60 * 60 * 1000) / (60 * 1000)).toInt(),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (task.id == 0L) "New Task" else "Edit Task") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    if (task.id != 0L) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete Task")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (title.isNotBlank()) {
                        scope.launch {
                            val finalRingtoneUri = ringtoneUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                            val newTask = task.copy(
                                title = title,
                                notes = notes,
                                dueDate = dueDate,
                                duration = duration,
                                repeat = repeat,
                                priority = priority,
                                backgroundColor = backgroundColor,
                                ringtone = finalRingtoneUri?.toString()
                            )

                            if (task.id == 0L) {
                                viewModel.insertTask(newTask)
                            } else {
                                viewModel.updateTask(newTask)
                            }

                            onNavigateBack()
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Save, null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it }, label = { Text("Task Name") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notes, onValueChange = { if (it.length <= 300) notes = it }, label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(), maxLines = 5, supportingText = { Text("${notes.length} / 300") }
            )
            OutlinedButton(onClick = { datePickerDialog.show() }) {
                Text(text = dueDate.toDateString())
            }
            OutlinedButton(onClick = { durationPickerDialog.show() }) {
                Text(text = "${duration / (60 * 60 * 1000)}h ${duration % (60 * 60 * 1000) / (60 * 1000)}m")
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

            Text("Priority", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.toDisplayString()) },
                        leadingIcon = { Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(p.toColor())) }
                    )
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
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(task)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }
}
