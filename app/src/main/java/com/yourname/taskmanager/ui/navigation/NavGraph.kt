package com.yourname.taskmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
                onNavigateToEditAlarm = { alarmId ->
                    navController.navigate(Screen.EditAlarm.createRoute(alarmId))
                },
                onNavigateToEditReminder = { reminderId ->
                    navController.navigate(Screen.EditReminder.createRoute(reminderId))
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
            AlarmListScreen(
                alarmViewModel = alarmViewModel,
                onNavigateToEditAlarm = { alarmId ->
                    navController.navigate(Screen.EditAlarm.createRoute(alarmId))
                }
            )
        }
        composable(BottomNavItem.Reminder.route) {
            ReminderListScreen(
                reminderViewModel = reminderViewModel,
                onNavigateToEditReminder = { reminderId ->
                    navController.navigate(Screen.EditReminder.createRoute(reminderId))
                }
            )
        }

        composable(Screen.AddTask.route) {
            AddEditTaskLoader(
                taskId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId")
            AddEditTaskLoader(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddAlarm.route) {
            AddEditAlarmLoader(alarmId = null, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.EditAlarm.route,
            arguments = listOf(navArgument("alarmId") { type = NavType.LongType })
        ) { backStackEntry ->
            val alarmId = backStackEntry.arguments?.getLong("alarmId")
            AddEditAlarmLoader(
                alarmId = alarmId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddReminder.route) {
            AddEditReminderLoader(reminderId = null, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.EditReminder.route,
            arguments = listOf(navArgument("reminderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getLong("reminderId")
            AddEditReminderLoader(
                reminderId = reminderId,
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

        composable(Screen.ManageCategories.route) {
            ManageCategoriesScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
