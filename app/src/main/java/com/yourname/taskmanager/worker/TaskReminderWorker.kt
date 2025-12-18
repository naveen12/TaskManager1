package com.yourname.taskmanager.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yourname.taskmanager.utils.createNotificationChannel
import com.yourname.taskmanager.utils.showNotification

class TaskReminderWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("EXTRA_TITLE") ?: return Result.failure()
        val message = inputData.getString("EXTRA_DESCRIPTION") ?: return Result.failure()

        createNotificationChannel(context)
        showNotification(context, title, message)

        return Result.success()
    }
}
