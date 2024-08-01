package com.kt_study.todo_alarm.categories

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kt_study.todo_alarm.categories.contents.Content

@Entity(tableName = "table_category")
data class Category(
    @PrimaryKey
    val id: Long = 0L,
    var title: String = "",
    val todo: String ="",
    val contents: MutableList<Content> = mutableListOf(),
)
