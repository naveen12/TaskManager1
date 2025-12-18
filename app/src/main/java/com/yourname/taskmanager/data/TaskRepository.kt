package com.yourname.taskmanager.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val activeTasks: Flow<List<Task>> = taskDao.getActiveTasks()
    val completedTasks: Flow<List<Task>> = taskDao.getCompletedTasks()
    val categories: Flow<List<String>> = taskDao.getAllCategories()

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.copy(modifiedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)
    }

    fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasks(query)
    }

    fun getTasksByCategory(category: String): Flow<List<Task>> {
        return taskDao.getTasksByCategory(category)
    }

    fun getUpcomingReminders(): Flow<List<Task>> {
        return taskDao.getUpcomingReminders(System.currentTimeMillis())
    }

    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }

    suspend fun toggleTaskCompletion(task: Task) {
        updateTask(task.copy(isCompleted = !task.isCompleted))
    }
}
