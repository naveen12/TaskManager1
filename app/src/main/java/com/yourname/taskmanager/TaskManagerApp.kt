package com.yourname.taskmanager

import android.app.Application
import com.yourname.taskmanager.data.AlarmRepository
import com.yourname.taskmanager.data.ReminderRepository
import com.yourname.taskmanager.data.TaskDatabase
import com.yourname.taskmanager.data.TaskRepository

class TaskManagerApp : Application() {
    val database by lazy { TaskDatabase.getDatabase(this) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val alarmRepository by lazy { AlarmRepository(database.alarmDao()) }
    val reminderRepository by lazy { ReminderRepository(database.reminderDao()) }
}
