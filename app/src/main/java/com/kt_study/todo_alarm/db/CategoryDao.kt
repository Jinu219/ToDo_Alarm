package com.kt_study.todo_alarm.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kt_study.todo_alarm.categories.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM table_category")
    fun getAllCategories(): LiveData<List<Category>>

    @Insert
    fun insertCategory(categoryEntity: Category)

    @Update
    fun updateCategory(categoryEntity: Category)

    @Delete
    fun deleteCategory(categoryEntity: Category)

}