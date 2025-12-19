package com.yourname.taskmanager.ui.navigation

sealed class Screen(val route: String) {
    object TaskList : Screen("taskList")
    object AddTask : Screen("addTask")
    object EditTask : Screen("editTask/{taskId}") {
        fun createRoute(taskId: Long) = "editTask/$taskId"
    }
    object AddAlarm : Screen("addAlarm")
    object EditAlarm : Screen("editAlarm/{alarmId}") {
        fun createRoute(alarmId: Long) = "editAlarm/$alarmId"
    }
    object AddReminder : Screen("addReminder")
    object EditReminder : Screen("editReminder/{reminderId}") {
        fun createRoute(reminderId: Long) = "editReminder/$reminderId"
    }
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Statistics : Screen("statistics")
    object ManageCategories : Screen("manageCategories")
}
