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
import com.yourname.taskmanager.data.Alarm
import com.yourname.taskmanager.ui.viewmodel.AlarmViewModel
import com.yourname.taskmanager.utils.toTimeString
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAlarmScreen(
    alarmViewModel: AlarmViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    alarm: Alarm
) {
    var alarmName by remember { mutableStateOf(alarm.name ?: "") }
    var ringtoneUri by remember { mutableStateOf(alarm.ringtone?.takeIf { it.isNotBlank() && it != "null" }?.let { Uri.parse(it) }) }
    var vibrate by remember { mutableStateOf(alarm.vibrate) }
    var time by remember { mutableStateOf(alarm.time) }

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
            calendar.time = Date(time)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            time = calendar.timeInMillis
        },
        Calendar.getInstance().apply { this.time = Date(time) }.get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().apply { this.time = Date(time) }.get(Calendar.MINUTE),
        false
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alarm.id == 0L) "Add Alarm" else "Edit Alarm") },
                actions = {
                    if (alarm.id != 0L) {
                        IconButton(onClick = {
                            alarmViewModel.deleteAlarm(alarm)
                            onNavigateBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Alarm")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val finalRingtoneUri = ringtoneUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    val newAlarm = alarm.copy(
                        name = alarmName,
                        time = time,
                        ringtone = finalRingtoneUri?.toString(),
                        vibrate = vibrate
                    )
                    alarmViewModel.insertAlarm(newAlarm)
                    onNavigateBack()
                }
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Alarm")
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
                value = alarmName,
                onValueChange = { alarmName = it },
                label = { Text("Alarm Name (Optional)") },
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
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
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
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Vibrate", modifier = Modifier.weight(1f))
                Switch(checked = vibrate, onCheckedChange = { vibrate = it })
            }
        }
    }
}
