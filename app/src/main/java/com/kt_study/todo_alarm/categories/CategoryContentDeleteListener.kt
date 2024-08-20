package com.kt_study.todo_alarm.categories

import com.kt_study.todo_alarm.categories.contents.ContentItem

interface CategoryContentDeleteListener {
    fun onContentDelete(categoryPosition: Int, contentItem: ContentItem)
}