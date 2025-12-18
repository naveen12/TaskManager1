package com.yourname.taskmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.taskmanager.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onExportData: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
    var localIsDarkTheme by remember { mutableStateOf(isDarkTheme) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        settingsViewModel.onThemeChange(localIsDarkTheme)
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Done, "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Dark Theme", modifier = Modifier.weight(1f))
                Switch(
                    checked = localIsDarkTheme,
                    onCheckedChange = { localIsDarkTheme = it }
                )
            }
            ListItem(
                headlineContent = { Text("Export Data") },
                modifier = Modifier.clickable { onExportData() }
            )
        }
    }
}
