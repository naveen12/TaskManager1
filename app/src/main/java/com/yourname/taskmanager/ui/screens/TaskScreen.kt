package com.yourname.taskmanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.taskmanager.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel = viewModel(),
    onNavigateToEditTask: (Long) -> Unit
) {
    val tasks by taskViewModel.activeTasks.collectAsState(initial = emptyList())
    val completedTasks by taskViewModel.completedTasks.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tasks") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "Active Tasks",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onTaskClick = { onNavigateToEditTask(task.id) },
                    onCheckedChange = { taskViewModel.toggleTaskCompletion(task) }
                )
            }

            item {
                Text(
                    text = "Completed Tasks",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(completedTasks) { task ->
                TaskItem(
                    task = task,
                    onTaskClick = { onNavigateToEditTask(task.id) },
                    onCheckedChange = { taskViewModel.toggleTaskCompletion(task) }
                )
            }
        }
    }
}
