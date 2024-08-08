package com.kt_study.todo_alarm.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ToDoListRepository(
    private val categoryDao: CategoryDao,
    private val contentDao: ContentDao
) {

    val getAllContent: Flow<List<ContentEntity>> = contentDao.getAllContents()
    val getAllCategory: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    // Insert
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertContent(content: ContentEntity) {
        contentDao.insertContent(content)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    // Update

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateCategory(category: CategoryEntity){
        categoryDao.updateCategory(category)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateContent(content: ContentEntity){
        contentDao.updateContent(content)
    }
}