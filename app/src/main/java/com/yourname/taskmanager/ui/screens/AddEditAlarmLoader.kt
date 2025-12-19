package com.yourname.taskmanager.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.taskmanager.data.Alarm
import com.yourname.taskmanager.ui.viewmodel.AlarmViewModel

@Composable
fun AddEditAlarmLoader(
    alarmId: Long?,
    viewModel: AlarmViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var alarm by remember { mutableStateOf<Alarm?>(null) }

    LaunchedEffect(alarmId) {
        if (alarmId != null && alarmId != 0L) {
            viewModel.getAlarmById(alarmId).collect { alarm = it }
        } else {
            alarm = Alarm(name = "", time = System.currentTimeMillis())
        }
    }

    alarm?.let {
        AddEditAlarmScreen(alarm = it, onNavigateBack = onNavigateBack)
    }
}
