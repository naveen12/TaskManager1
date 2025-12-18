package com.yourname.taskmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.taskmanager.ui.navigation.Screen
import com.yourname.taskmanager.ui.viewmodel.AlarmViewModel
import com.yourname.taskmanager.ui.viewmodel.CalendarViewModel
import com.yourname.taskmanager.ui.viewmodel.ReminderViewModel
import com.yourname.taskmanager.ui.viewmodel.TaskViewModel
import com.yourname.taskmanager.utils.toLocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleifyScreen(
    taskViewModel: TaskViewModel = viewModel(),
    alarmViewModel: AlarmViewModel = viewModel(),
    reminderViewModel: ReminderViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel(),
    onNavigateToEditTask: (Long) -> Unit,
    onNavigateToAddItem: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val tasks by taskViewModel.activeTasks.collectAsState(initial = emptyList())
    val completedTasks by taskViewModel.completedTasks.collectAsState(initial = emptyList())
    val alarms by alarmViewModel.allAlarms.collectAsState(initial = emptyList())
    val reminders by reminderViewModel.allReminders.collectAsState(initial = emptyList())
    val selectedDate by calendarViewModel.selectedDate.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scheduleify") },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, null) }
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

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        text = "Tasks",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items((tasks + completedTasks).filter { it.dueDate?.toLocalDate() == selectedDate }) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = { onNavigateToEditTask(task.id) },
                        onCheckedChange = { taskViewModel.toggleTaskCompletion(task) },
                    )
                }

                item {
                    Text(
                        text = "Alarms",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(alarms.filter { it.time.toLocalDate() == selectedDate }) { alarm ->
                    AlarmItem(alarm = alarm)
                }

                item {
                    Text(
                        text = "Reminders",
                        style = androidx.compose.material3.MaterialTheme. typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(reminders.filter { it.time.toLocalDate() == selectedDate }) { reminder ->
                    ReminderItem(reminder = reminder)
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
    }
}
