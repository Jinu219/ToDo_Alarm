package com.kt_study.todo_alarm.categories

import com.kt_study.todo_alarm.categories.contents.ContentItem

data class CategoryItem(
    val id: Long,
    val title: String = "",
    val contents: MutableList<ContentItem> = mutableListOf(),
)