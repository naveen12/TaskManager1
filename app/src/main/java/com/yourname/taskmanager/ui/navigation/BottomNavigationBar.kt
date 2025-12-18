package com.yourname.taskmanager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Scheduleify,
        BottomNavItem.Task,
        BottomNavItem.Alarm,
        BottomNavItem.Reminder
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach {
            NavigationBarItem(
                icon = { Icon(it.icon, contentDescription = it.title) },
                label = { Text(it.title) },
                selected = currentRoute == it.route,
                onClick = {
                    navController.navigate(it.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val title: String) {
    object Scheduleify : BottomNavItem("scheduleify", Icons.Default.Home, "Scheduleify")
    object Task : BottomNavItem("task", Icons.Default.Task, "Task")
    object Alarm : BottomNavItem("alarm", Icons.Default.Alarm, "Alarm")
    object Reminder : BottomNavItem("reminder", Icons.Default.Notifications, "Reminder")
}
