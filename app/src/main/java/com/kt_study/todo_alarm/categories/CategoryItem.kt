package com.kt_study.todo_alarm.categories

import com.kt_study.todo_alarm.categories.contents.ContentItem

data class CategoryItem(
    val id: Int,
    val title: String = "",
    var contents: MutableList<ContentItem> = mutableListOf(),
)