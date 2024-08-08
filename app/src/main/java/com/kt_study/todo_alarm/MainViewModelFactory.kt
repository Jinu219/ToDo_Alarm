package com.kt_study.todo_alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kt_study.todo_alarm.db.ToDoListRepository

class MainViewModelFactory(private val repository: ToDoListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}