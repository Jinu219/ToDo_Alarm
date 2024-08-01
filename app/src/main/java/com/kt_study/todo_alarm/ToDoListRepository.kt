package com.kt_study.todo_alarm

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.kt_study.todo_alarm.categories.Category
import com.kt_study.todo_alarm.categories.CategoryDao
import com.kt_study.todo_alarm.categories.contents.Content
import com.kt_study.todo_alarm.categories.contents.ContentDao

class ToDoListRepository(context: Context) {
    private val contentDao: ContentDao = ToDoListDatabase.getInstance(context).contentDao()
    private val categoryDao: CategoryDao = ToDoListDatabase.getInstance(context).categoryDao()

    val getAllContent: LiveData<List<Content>> = contentDao.getAllContents()
    val getAllCategory: LiveData<List<Category>> = categoryDao.getAllCategories()


    suspend fun addContent(content: Content) {
        contentDao.insertContent(content)
    }

    suspend fun addCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    companion object{
        @Volatile
        private var INSTANCE: ToDoListRepository? = null

        fun getInstance(context: Context): ToDoListRepository{
            return INSTANCE ?: synchronized(this){
                val instance = ToDoListRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}