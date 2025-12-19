package com.yourname.taskmanager.data

import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {

    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()

    fun getReminderById(id: Long): Flow<Reminder> {
        return reminderDao.getReminderById(id)
    }

    suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }
}
