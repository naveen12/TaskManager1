package com.yourname.taskmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yourname.taskmanager.ui.screens.*
import com.yourname.taskmanager.ui.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    taskViewModel: TaskViewModel = viewModel(),
    alarmViewModel: AlarmViewModel = viewModel(),
    reminderViewModel: ReminderViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    onExportDatabase: () -> Unit,
    onImportDatabase: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Scheduleify.route
    ) {
        composable(BottomNavItem.Scheduleify.route) {
            ScheduleifyScreen(
                taskViewModel = taskViewModel,
                alarmViewModel = alarmViewModel,
                reminderViewModel = reminderViewModel,
                calendarViewModel = calendarViewModel,
                onNavigateToEditTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                },
                onNavigateToAddItem = { route -> navController.navigate(route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(BottomNavItem.Task.route) {
            TaskScreen(
                taskViewModel = taskViewModel,
                onNavigateToEditTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                }
            )
        }
        composable(BottomNavItem.Alarm.route) {
            AlarmListScreen(alarmViewModel = alarmViewModel)
        }
        composable(BottomNavItem.Reminder.route) {
            ReminderListScreen(reminderViewModel = reminderViewModel)
        }

        composable(Screen.AddTask.route) {
            AddEditTaskScreen(
                viewModel = taskViewModel,
                taskId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            AddEditTaskScreen(
                viewModel = taskViewModel,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onExportData = onExportDatabase,
                settingsViewModel = settingsViewModel
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                viewModel = taskViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddAlarm.route) {
            AddEditAlarmScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.AddReminder.route) {
            AddEditReminderScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.ManageCategories.route) {
            ManageCategoriesScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
