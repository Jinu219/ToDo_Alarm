package com.kt_study.todo_alarm

import android.app.Application
import com.kt_study.todo_alarm.db.ToDoListDatabase
import com.kt_study.todo_alarm.db.ToDoListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ToDoApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy {
        ToDoListDatabase.getInstance(this, applicationScope)
    }
    val repository by lazy {
        ToDoListRepository(database.categoryDao(), database.contentDao())
    }
}