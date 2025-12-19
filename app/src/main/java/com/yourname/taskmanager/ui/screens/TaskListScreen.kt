package com.yourname.taskmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.taskmanager.data.Alarm
import com.yourname.taskmanager.data.Reminder
import com.yourname.taskmanager.data.Task
import com.yourname.taskmanager.ui.navigation.Screen
import com.yourname.taskmanager.ui.viewmodel.AlarmViewModel
import com.yourname.taskmanager.ui.viewmodel.CalendarViewModel
import com.yourname.taskmanager.ui.viewmodel.ReminderViewModel
import com.yourname.taskmanager.ui.viewmodel.TaskViewModel
import com.yourname.taskmanager.utils.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(
    taskViewModel: TaskViewModel = viewModel(),
    alarmViewModel: AlarmViewModel = viewModel(),
    reminderViewModel: ReminderViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel(),
    onNavigateToAddItem: (String) -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToManageCategories: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onExportDatabase: () -> Unit
) {
    val tasks by taskViewModel.activeTasks.collectAsState(initial = emptyList())
    val completedTasks by taskViewModel.completedTasks.collectAsState(initial = emptyList())
    val alarms by alarmViewModel.allAlarms.collectAsState(initial = emptyList())
    val reminders by reminderViewModel.allReminders.collectAsState(initial = emptyList())
    val selectedDate by calendarViewModel.selectedDate.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, "Search")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Statistics") },
                            onClick = {
                                showMenu = false
                                onNavigateToStatistics()
                            },
                            leadingIcon = { Icon(Icons.Default.BarChart, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Manage Categories") },
                            onClick = {
                                showMenu = false
                                onNavigateToManageCategories()
                            },
                            leadingIcon = { Icon(Icons.Default.Category, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Export Database") },
                            onClick = {
                                showMenu = false
                                onExportDatabase()
                            },
                            leadingIcon = { Icon(Icons.Default.Upload, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Completed") },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CalendarView(onDateSelected = { date -> calendarViewModel.onDateSelected(date) })

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { 
                    Text("Tasks", style = MaterialTheme.typography.headlineMedium)
                }
                items((tasks + completedTasks).filter { it.dueDate.toLocalDate() == selectedDate }) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = { onNavigateToEditTask(task.id) },
                        onCheckedChange = { taskViewModel.toggleTaskCompletion(task) },
                    )
                }
                item { 
                    Text("Alarms", style = MaterialTheme.typography.headlineMedium)
                }
                items(alarms.filter { it.time.toLocalDate() == selectedDate }) { alarm ->
                    AlarmItem(alarm = alarm)
                }
                item { 
                    Text("Reminders", style = MaterialTheme.typography.headlineMedium)
                }
                items(reminders.filter { it.time.toLocalDate() == selectedDate }) { reminder ->
                    ReminderItem(reminder = reminder)
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
            Column(modifier = Modifier.navigationBarsPadding()) {
                ListItem(
                    headlineContent = { Text("Add Task") },
                    leadingContent = { Icon(Icons.Default.Task, null) },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        onNavigateToAddItem(Screen.AddTask.route)
                    }
                )
                ListItem(
                    headlineContent = { Text("Add Alarm") },
                    leadingContent = { Icon(Icons.Default.Alarm, null) },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        onNavigateToAddItem(Screen.AddAlarm.route)
                    }
                )
                ListItem(
                    headlineContent = { Text("Add Reminder") },
                    leadingContent = { Icon(Icons.Default.Notifications, null) },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        onNavigateToAddItem(Screen.AddReminder.route)
                    }
                )
            }
        }
    }

    // Delete Completed Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete All Completed Tasks?") },
            text = { Text("This will permanently delete all completed tasks.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskViewModel.deleteCompletedTasks()
                        showDeleteDialog = false
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

@Composable
fun AlarmItem(alarm: Alarm) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = alarm.time.toTimeString(), style = MaterialTheme.typography.headlineSmall)
            Switch(checked = alarm.isEnabled, onCheckedChange = { /* TODO */ })
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = reminder.title, style = MaterialTheme.typography.titleMedium)
                Text(text = reminder.time.toTimeString(), style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.Default.Notifications, null)
        }
    }
}

@Composable
fun EmptyContentPlaceholder(contentType: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val icon = when {
                contentType.startsWith("Tasks") -> Icons.Default.Task
                contentType.startsWith("Alarms") -> Icons.Default.Alarm
                contentType.startsWith("Reminders") -> Icons.Default.Notifications
                else -> Icons.Default.Inbox
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No $contentType yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onCheckedChange() }
            )

            Spacer(Modifier.width(12.dp))

            // Task content
            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Description/Notes
                if (task.notes.isNotEmpty()) {
                    Text(
                        text = task.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Due Date & Reminder
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (task.dueDate.isOverdue() && !task.isCompleted)
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = task.dueDate.toRelativeDateString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (task.dueDate.isOverdue() && !task.isCompleted)
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Priority indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(task.priority.toColor())
            )
        }
    }
}
