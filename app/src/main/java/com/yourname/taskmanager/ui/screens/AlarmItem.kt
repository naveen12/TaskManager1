package com.yourname.taskmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourname.taskmanager.data.Alarm
import com.yourname.taskmanager.utils.toTimeString

@Composable
fun AlarmItem(
    alarm: Alarm,
    onAlarmEnabledChange: (Boolean) -> Unit,
    onAlarmClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAlarmClick() },
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
                Text(text = alarm.name ?: "Alarm", style = MaterialTheme.typography.headlineSmall)
                Text(text = alarm.time.toTimeString(), style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = alarm.isEnabled, onCheckedChange = onAlarmEnabledChange)
        }
    }
}
