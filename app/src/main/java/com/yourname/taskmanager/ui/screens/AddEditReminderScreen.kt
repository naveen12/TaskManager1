package com.yourname.taskmanager.ui.screens

import android.app.TimePickerDialog
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.taskmanager.data.Reminder
import com.yourname.taskmanager.ui.viewmodel.ReminderViewModel
import com.yourname.taskmanager.utils.toTimeString
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReminderScreen(
    reminderViewModel: ReminderViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    reminder: Reminder? = null
) {
    var title by remember { mutableStateOf(reminder?.title ?: "") }
    var ringtoneUri by remember { mutableStateOf(reminder?.ringtone?.takeIf { it.isNotBlank() && it != "null" }?.let { Uri.parse(it) }) }
    var time by remember { mutableStateOf(reminder?.time ?: System.currentTimeMillis()) }

    val context = LocalContext.current
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

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            time = calendar.timeInMillis
        },
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        false
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (reminder == null) "Add Reminder" else "Edit Reminder") },
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
                        title = title,
                        time = time,
                        ringtone = finalRingtoneUri?.toString()
                    ) ?: Reminder(
                        title = title,
                        time = time,
                        ringtone = finalRingtoneUri?.toString()
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
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { timePickerDialog.show() }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Time", style = MaterialTheme.typography.bodyLarge)
                    Text(time.toTimeString(), style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                        ringtonePickerLauncher.launch(intent)
                    }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ringtone", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = ringtoneUri?.let { uri ->
                            RingtoneManager.getRingtone(context, uri)?.getTitle(context)
                        } ?: "Default",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
