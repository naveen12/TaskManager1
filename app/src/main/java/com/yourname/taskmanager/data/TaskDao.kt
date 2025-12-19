package com.yourname.taskmanager.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Get all tasks
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, priority DESC")
    fun getAllTasks(): Flow<List<Task>>

    // Get active tasks (not completed)
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getActiveTasks(): Flow<List<Task>>

    // Get completed tasks
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY modifiedAt DESC")
    fun getCompletedTasks(): Flow<List<Task>>

    // Get task by ID
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?

    // Search tasks
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%'")
    fun searchTasks(query: String): Flow<List<Task>>

    // Insert task
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    // Update task
    @Update
    suspend fun updateTask(task: Task)

    // Delete task
    @Delete
    suspend fun deleteTask(task: Task)

    // Delete all completed tasks
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()
}
