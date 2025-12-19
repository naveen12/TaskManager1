package com.yourname.taskmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val dueDate: Long = System.currentTimeMillis(),
    val duration: Long = 0,
    val repeat: String = "Does not repeat",
    val priority: Priority = Priority.LOW,
    val backgroundColor: String = "#FFFFFF",
    val ringtone: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
