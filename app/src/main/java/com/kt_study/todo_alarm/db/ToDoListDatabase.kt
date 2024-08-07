package com.kt_study.todo_alarm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [CategoryEntity::class, ContentEntity::class], version = 1)
abstract class ToDoListDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: ToDoListDatabase? = null

        @Synchronized
        fun getInstance(context: Context, scope: CoroutineScope): ToDoListDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoListDatabase::class.java,
                    "todolist.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }


}