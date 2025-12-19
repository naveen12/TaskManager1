package com.yourname.taskmanager.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.taskmanager.data.Task
import com.yourname.taskmanager.ui.viewmodel.TaskViewModel

@Composable
fun AddEditTaskLoader(
    taskId: Long?,
    viewModel: TaskViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var task by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(taskId) {
        if (taskId != null && taskId != 0L) {
            task = viewModel.getTaskById(taskId)
        } else {
            task = Task(title = "", dueDate = System.currentTimeMillis())
        }
    }

    task?.let {
        AddEditTaskScreen(viewModel = viewModel, task = it, onNavigateBack = onNavigateBack)
    }
}
