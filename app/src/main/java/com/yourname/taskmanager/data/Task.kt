package com.yourname.taskmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val notes: String = "",
    val dueDate: Long? = null,          // Timestamp in millis
    val reminderTime: Long? = null,     // Timestamp for alarm
    val ringtone: String? = null,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.LOW,
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
