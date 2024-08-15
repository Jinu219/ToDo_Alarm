package com.kt_study.todo_alarm.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Query("SELECT * FROM table_content")
    fun getAllContents(): Flow<List<ContentEntity>>

    @Insert
    fun insertContent(contentEntity: ContentEntity)

    @Update
    fun updateContent(contentEntity: ContentEntity)

    @Delete
    fun deleteContent(contentEntity: ContentEntity)

    @Query("DELETE FROM table_content")
    fun deleteAllContents()

    @Query("SELECT * FROM table_content WHERE categoryId = :categoryId")
    fun getContentsByCategoryId(categoryId: Int): Flow<List<ContentEntity>>

    @Query("SELECT MAX(contentId) FROM table_content")
    suspend fun getMaxContentId(): Int?

}