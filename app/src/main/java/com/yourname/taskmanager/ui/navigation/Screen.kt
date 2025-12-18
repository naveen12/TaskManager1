package com.yourname.taskmanager.ui.navigation

sealed class Screen(val route: String) {
    object TaskList : Screen("task_list")
    object AddTask : Screen("add_task")
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: Long) = "edit_task/$taskId"
    }
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Statistics : Screen("statistics")
    object AddAlarm : Screen("add_alarm")
    object AddReminder : Screen("add_reminder")
    object ManageCategories : Screen("manage_categories")
}
