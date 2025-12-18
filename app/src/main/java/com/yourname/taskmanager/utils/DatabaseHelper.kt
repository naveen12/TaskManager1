package com.yourname.taskmanager.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.yourname.taskmanager.data.TaskDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object DatabaseHelper {

    fun exportDatabase(context: Context): Uri? {
        return try {
            val currentDBPath = TaskDatabase.getDatabasePath(context)
            val currentDB = File(currentDBPath)

            if (!currentDB.exists()) return null

            // Create export file in cache directory
            val exportFile = File(context.cacheDir, "task_manager_backup_${System.currentTimeMillis()}.db")

            // Copy database
            FileInputStream(currentDB).use { input ->
                FileOutputStream(exportFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Return URI using FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                exportFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun importDatabase(context: Context, uri: Uri): Boolean {
        return try {
            val currentDBPath = TaskDatabase.getDatabasePath(context)
            val currentDB = File(currentDBPath)

            // Copy from URI to database location
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(currentDB).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun createEmailIntent(context: Context, uri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Task Manager Database Backup")
            putExtra(Intent.EXTRA_TEXT, "Task Manager database backup file attached.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
