package com.yourname.taskmanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.taskmanager.TaskManagerApp
import com.yourname.taskmanager.data.Priority
import com.yourname.taskmanager.data.Task
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskRepository = (application as TaskManagerApp).taskRepository

    // State flows
    val allTasks = taskRepository.allTasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val activeTasks = taskRepository.activeTasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val completedTasks = taskRepository.completedTasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // UI State
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                taskRepository.searchTasks(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Task operations
    suspend fun insertTask(task: Task): Long {
        return taskRepository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.deleteTask(task)
    }

    fun toggleTaskCompletion(task: Task) = viewModelScope.launch {
        updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    fun deleteCompletedTasks() = viewModelScope.launch {
        taskRepository.deleteCompletedTasks()
    }

    suspend fun getTaskById(id: Long): Task? {
        return taskRepository.getTaskById(id)
    }

    // Search
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    // Statistics
    val taskStats = allTasks.map { tasks ->
        TaskStats(
            total = tasks.size,
            completed = tasks.count { it.isCompleted },
            active = tasks.count { !it.isCompleted },
            highPriority = tasks.count { it.priority == Priority.HIGH && !it.isCompleted },
            overdue = tasks.count {
                it.dueDate < System.currentTimeMillis() && !it.isCompleted
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskStats())
}

// UI State
data class TaskUiState(
    val filteredTasks: List<Task>? = null,
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// Statistics
data class TaskStats(
    val total: Int = 0,
    val completed: Int = 0,
    val active: Int = 0,
    val highPriority: Int = 0,
    val overdue: Int = 0
)
