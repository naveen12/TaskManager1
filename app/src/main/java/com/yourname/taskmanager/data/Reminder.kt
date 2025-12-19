package com.yourname.taskmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val dueDate: Long = System.currentTimeMillis(),
    val time: Long,
    val repeat: String = "Does not repeat",
    val category: String = "Default",
    val backgroundColor: String = "#FFFFFF",
    val ringtone: String? = null
)
