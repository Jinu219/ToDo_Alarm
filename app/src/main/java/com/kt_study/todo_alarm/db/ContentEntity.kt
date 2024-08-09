package com.kt_study.todo_alarm.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_content")
data class ContentEntity(
    @PrimaryKey
    val id: Int = 0,
    val categoryId: Long = 0L,
    var toDo: String = "",
    var hour: Int = 0,
    var min: Int = 0,
    var checked: Boolean = false
)