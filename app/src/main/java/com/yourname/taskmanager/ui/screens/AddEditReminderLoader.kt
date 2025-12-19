package com.yourname.taskmanager.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.taskmanager.data.Reminder
import com.yourname.taskmanager.ui.viewmodel.ReminderViewModel

@Composable
fun AddEditReminderLoader(
    reminderId: Long?,
    viewModel: ReminderViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var reminder by remember { mutableStateOf<Reminder?>(null) }

    LaunchedEffect(reminderId) {
        if (reminderId != null && reminderId != 0L) {
            viewModel.getReminderById(reminderId).collect { reminder = it }
        } else {
            reminder = Reminder(title = "", time = System.currentTimeMillis())
        }
    }

    reminder?.let {
        AddEditReminderScreen(reminder = it, onNavigateBack = onNavigateBack)
    }
}
