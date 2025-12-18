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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* 
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yourname.taskmanager.alarm.AlarmScheduler
import com.yourname.taskmanager.data.Priority
import com.yourname.taskmanager.data.Task
import com.yourname.taskmanager.ui.viewmodel.TaskViewModel
import com.yourname.taskmanager.utils.toColor
import com.yourname.taskmanager.utils.toDateString
import com.yourname.taskmanager.utils.toDisplayString
import com.yourname.taskmanager.utils.toTimeString
import kotlinx.coroutines.launch
import java.util. *

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    taskId: Long?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val alarmScheduler = remember { AlarmScheduler(context) }
    val scope = rememberCoroutineScope()

    var task by remember { mutableStateOf<Task?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var ringtoneUri by remember { mutableStateOf<Uri?>(null) }
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

    // Load existing task
    LaunchedEffect(taskId) {
        if (taskId != null && taskId != 0L) {
            viewModel.getTaskById(taskId)?.let { existingTask ->
                task = existingTask
                title = existingTask.title
                description = existingTask.description
                notes = existingTask.notes
                category = existingTask.category
                priority = existingTask.priority
                dueDate = existingTask.dueDate
                reminderTime = existingTask.reminderTime
                ringtoneUri = existingTask.ringtone?.takeIf { it.isNotBlank() && it != "null" }?.let { Uri.parse(it) }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null || taskId == 0L) "New Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (taskId != null && taskId != 0L) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete Task")
                        }
                    }
                }
            )
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
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Title, null) }
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                leadingIcon = { Icon(Icons.Default.Description, null) }
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 6,
                leadingIcon = { Icon(Icons.Default.Notes, null) }
            )

            // Category
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Category, null) }
            )

            // Priority Selection
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
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(p.toColor())
                            )
                        }
                    )
                }
            }

            // Due Date
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val calendar = Calendar.getInstance()
                    dueDate?.let { calendar.timeInMillis = it }

                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            calendar.set(year, month, day)
                            dueDate = calendar.timeInMillis
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Due Date", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = dueDate?.toDateString() ?: "Not set",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (dueDate != null) {
                        IconButton(onClick = { dueDate = null }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    } else {
                        Icon(Icons.Default.CalendarToday, null)
                    }
                }
            }

            // Reminder Time
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val calendar = Calendar.getInstance()
                    reminderTime?.let { calendar.timeInMillis = it }

                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)
                            reminderTime = calendar.timeInMillis
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false
                    ).show()
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Reminder", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = reminderTime?.toTimeString() ?: "Not set",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (reminderTime != null) {
                        IconButton(onClick = { reminderTime = null }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    } else {
                        Icon(Icons.Default.Alarm, null)
                    }
                }
            }

            // Ringtone
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                    ringtonePickerLauncher.launch(intent)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Ringtone", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = ringtoneUri?.let { uri ->
                                RingtoneManager.getRingtone(context, uri)?.getTitle(context)
                            } ?: "Default",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Icon(Icons.Default.MusicNote, null)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        scope.launch {
                            val finalRingtoneUri = ringtoneUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                            val newTask = task?.copy(
                                title = title,
                                description = description,
                                notes = notes,
                                category = category,
                                priority = priority,
                                dueDate = dueDate,
                                reminderTime = reminderTime,
                                ringtone = finalRingtoneUri?.toString()
                            ) ?: Task(
                                title = title,
                                description = description,
                                notes = notes,
                                category = category,
                                priority = priority,
                                dueDate = dueDate,
                                reminderTime = reminderTime,
                                ringtone = finalRingtoneUri?.toString()
                            )

                            // Insert or update task
                            if (taskId == null || taskId == 0L) {
                                val newTaskId = viewModel.insertTask(newTask)
                                reminderTime?.let {
                                    alarmScheduler.schedule(newTask.copy(id = newTaskId))
                                }
                            } else {
                                viewModel.updateTask(newTask)
                                reminderTime?.let {
                                    alarmScheduler.schedule(newTask)
                                } ?: alarmScheduler.cancelTask(taskId)
                            }

                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Save Task")
            }

        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        task?.let { taskToDelete ->
                            viewModel.deleteTask(taskToDelete)
                            alarmScheduler.cancelTask(taskToDelete.id)
                        }
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
