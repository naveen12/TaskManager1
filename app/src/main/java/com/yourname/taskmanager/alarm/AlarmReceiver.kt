package com.yourname.taskmanager.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.yourname.taskmanager.MainActivity
import com.yourname.taskmanager.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("EXTRA_TYPE") ?: return
        val id = intent.getLongExtra("EXTRA_ID", -1L)
        val title = intent.getStringExtra("EXTRA_TITLE") ?: ""
        val description = intent.getStringExtra("EXTRA_DESCRIPTION") ?: ""

        if (id != -1L) {
            showNotification(context, type, id, title, description)
        }
    }

    private fun showNotification(
        context: Context,
        type: String,
        id: Long,
        title: String,
        description: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = when (type) {
            "TASK" -> "task_reminder_channel"
            "ALARM" -> "alarm_channel"
            "REMINDER" -> "reminder_channel"
            else -> return
        }

        val channelName = when (type) {
            "TASK" -> "Task Reminders"
            "ALARM" -> "Alarms"
            "REMINDER" -> "Reminders"
            else -> return
        }

        val ringtoneUri = when (type) {
            "ALARM" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                this.description = "Notifications for $channelName"
                enableVibration(true)
                setSound(ringtoneUri, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_ID", id)
            putExtra("EXTRA_TYPE", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(ringtoneUri)
            .build()

        notificationManager.notify(id.toInt(), notification)
    }
}
