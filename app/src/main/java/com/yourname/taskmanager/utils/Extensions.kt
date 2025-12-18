package com.yourname.taskmanager.utils

import com.yourname.taskmanager.data.Priority
import com.yourname.taskmanager.ui.theme.PriorityHigh
import com.yourname.taskmanager.ui.theme.PriorityLow
import com.yourname.taskmanager.ui.theme.PriorityMedium
import androidx.compose.ui.graphics.Color

// Priority extensions
fun Priority.toColor(): Color {
    return when (this) {
        Priority.LOW -> PriorityLow
        Priority.MEDIUM -> PriorityMedium
        Priority.HIGH -> PriorityHigh
    }
}

fun Priority.toDisplayString(): String {
    return when (this) {
        Priority.LOW -> "Low"
        Priority.MEDIUM -> "Medium"
        Priority.HIGH -> "High"
    }
}
