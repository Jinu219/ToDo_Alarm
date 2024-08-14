package com.kt_study.todo_alarm.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ToDoListRepository(
    private val categoryDao: CategoryDao,
    private val contentDao: ContentDao
) {

    val getAllContents: Flow<List<ContentEntity>> = contentDao.getAllContents()
    val getAllCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

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
    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateContent(content: ContentEntity) {
        contentDao.updateContent(content)
    }

    // Delete

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteContent(content: ContentEntity) {
        contentDao.deleteContent(content)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun clearDatabase() {
        categoryDao.deleteAllCategories()
        contentDao.deleteAllContents()
    }

    fun getContentsByCategoryId(categoryId: Int): Flow<List<ContentEntity>> {
        return contentDao.getContentsByCategoryId(categoryId)
    }

}