package com.kt_study.todo_alarm.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM table_category")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("DELETE FROM table_category")
    fun deleteAllCategories()

    @Query("SELECT MAX(id) FROM table_category")
    suspend fun getMaxCategoryId(): Int?

    @Insert
    fun insertCategory(categoryEntity: CategoryEntity)

    @Update
    fun updateCategory(categoryEntity: CategoryEntity)

    @Delete
    fun deleteCategory(categoryEntity: CategoryEntity)

}

