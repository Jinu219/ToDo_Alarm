package com.kt_study.todo_alarm.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_category")
data class CategoryEntity(
    @PrimaryKey
    val id: Int = 0,
    var title: String = "",
)