package com.kt_study.todo_alarm.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kt_study.todo_alarm.categories.contents.Content

@Dao
interface ContentDao {
    @Query("SELECT * FROM table_content")
    fun getAllContents(): LiveData<List<Content>>

    @Insert
    fun insertContent(contentEntity: Content)

    @Update
    fun updateContent(contentEntity: Content)

    @Delete
    fun deleteContent(contentEntity: Content)

}