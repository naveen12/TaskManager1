package com.yourname.taskmanager.alarm

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yourname.taskmanager.data.Alarm
import com.yourname.taskmanager.data.Reminder
import com.yourname.taskmanager.data.Task
import com.yourname.taskmanager.worker.AlarmWorker
import com.yourname.taskmanager.worker.ReminderWorker
import com.yourname.taskmanager.worker.TaskReminderWorker
import java.util.concurrent.TimeUnit

class AlarmScheduler(private val context: Context) {

    fun schedule(task: Task) {
        val reminderTime = task.reminderTime ?: return
        if (reminderTime < System.currentTimeMillis()) return

        val data = Data.Builder()
            .putLong("EXTRA_ID", task.id)
            .putString("EXTRA_TITLE", task.title)
            .putString("EXTRA_DESCRIPTION", task.description)
            .build()

        val delay = reminderTime - System.currentTimeMillis()
        val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(task.id.toString())
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun schedule(alarm: Alarm) {
        if (alarm.time < System.currentTimeMillis()) return

        val data = Data.Builder()
            .putLong("EXTRA_ID", alarm.id)
            .putString("EXTRA_TITLE", "Alarm")
            .putString("EXTRA_DESCRIPTION", "It's time!")
            .build()

        val delay = alarm.time - System.currentTimeMillis()
        val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(alarm.id.toString())
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun schedule(reminder: Reminder) {
        if (reminder.time < System.currentTimeMillis()) return

        val data = Data.Builder()
            .putLong("EXTRA_ID", reminder.id)
            .putString("EXTRA_TITLE", reminder.title)
            .putString("EXTRA_DESCRIPTION", "Reminder: ${reminder.title}")
            .build()

        val delay = reminder.time - System.currentTimeMillis()
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(reminder.id.toString())
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun cancelTask(taskId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag(taskId.toString())
    }

    fun cancelAlarm(alarmId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag(alarmId.toString())
    }

    fun cancelReminder(reminderId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag(reminderId.toString())
    }
}
