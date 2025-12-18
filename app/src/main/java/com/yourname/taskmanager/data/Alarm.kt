package com.yourname.taskmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String? = null,
    val time: Long,
    val ringtone: String? = null,
    val vibrate: Boolean = true,
    val isEnabled: Boolean = true,
    val repeatOption: String = "Doesn't repeat",
    val selectedDays: Set<String> = emptySet()
)
