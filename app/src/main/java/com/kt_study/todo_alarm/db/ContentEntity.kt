package com.kt_study.todo_alarm.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_content")
data class ContentEntity(
    @PrimaryKey
    val contentId: Int = 0,
    val categoryId: Int = 0,
    var toDo: String = "",
    var hour: Int = 0,
    var min: Int = 0,
    var checked: Boolean = false
)