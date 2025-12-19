package com.yourname.taskmanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.yourname.taskmanager.alarm.AlarmScheduler
import com.yourname.taskmanager.ui.navigation.BottomNavigationBar
import com.yourname.taskmanager.ui.navigation.NavGraph
import com.yourname.taskmanager.ui.theme.TaskManagerTheme
import com.yourname.taskmanager.ui.viewmodel.*
import com.yourname.taskmanager.utils.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var alarmScheduler: AlarmScheduler
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alarmScheduler = AlarmScheduler(this)
        settingsManager = SettingsManager(this)

        setContent {
            RequestPermissions(context = this) {}
            if (!hasExactAlarmPermission(this)) {
                requestExactAlarmPermission(this)
            }
            if (!hasOverlayPermission(this)) {
                requestOverlayPermission(this)
            }

            val settingsViewModel: SettingsViewModel = viewModel()
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            TaskManagerTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskManagerApp(settingsViewModel)
                }
            }
        }
    }

    @Composable
    fun TaskManagerApp(settingsViewModel: SettingsViewModel) {
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
                    settingsViewModel = settingsViewModel,
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
}
