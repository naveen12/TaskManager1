package com.yourname.taskmanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.yourname.taskmanager.alarm.AlarmScheduler
import com.yourname.taskmanager.ui.navigation.BottomNavigationBar
import com.yourname.taskmanager.ui.navigation.NavGraph
import com.yourname.taskmanager.ui.theme.TaskManagerTheme
import com.yourname.taskmanager.ui.viewmodel.AlarmViewModel
import com.yourname.taskmanager.ui.viewmodel.CalendarViewModel
import com.yourname.taskmanager.ui.viewmodel.ReminderViewModel
import com.yourname.taskmanager.ui.viewmodel.TaskViewModel
import com.yourname.taskmanager.utils.DatabaseHelper
import com.yourname.taskmanager.utils.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var alarmScheduler: AlarmScheduler
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alarmScheduler = AlarmScheduler(this)
        settingsManager = SettingsManager(this)

        // Request permissions
        requestNeededPermissions()

        setContent {
            TaskManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskManagerApp()
                }
            }
        }
    }

    @Composable
    fun TaskManagerApp() {
        val navController = rememberNavController()
        val taskViewModel: TaskViewModel = viewModel()
        val alarmViewModel: AlarmViewModel = viewModel()
        val reminderViewModel: ReminderViewModel = viewModel()
        val calendarViewModel: CalendarViewModel = viewModel()

        val importLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { importDatabase(it) }
        }

        Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavGraph(
                    navController = navController,
                    taskViewModel = taskViewModel,
                    alarmViewModel = alarmViewModel,
                    reminderViewModel = reminderViewModel,
                    calendarViewModel = calendarViewModel,
                    onExportDatabase = { exportDatabase() },
                    onImportDatabase = { importLauncher.launch("*/*") }
                )
            }
        }
    }

    private fun exportDatabase() {
        val uri = DatabaseHelper.exportDatabase(this)
        if (uri != null) {
            val emailIntent = DatabaseHelper.createEmailIntent(this, uri)
            try {
                startActivity(Intent.createChooser(emailIntent, "Send database backup via"))
            } catch (e: Exception) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Failed to export database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importDatabase(uri: Uri) {
        (application as TaskManagerApp).database.close()
        val success = DatabaseHelper.importDatabase(this, uri)
        if (success) {
            Toast.makeText(this, "Database imported successfully. Restarting...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Failed to import database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestNeededPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM), ALARM_PERMISSION_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle permission results if needed
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 100
        private const val ALARM_PERMISSION_CODE = 101
    }
}
