package com.yourname.taskmanager.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val activeTasks: Flow<List<Task>> = taskDao.getActiveTasks()
    val completedTasks: Flow<List<Task>> = taskDao.getCompletedTasks()

    suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)
    }

    fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasks(query)
    }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }
}
