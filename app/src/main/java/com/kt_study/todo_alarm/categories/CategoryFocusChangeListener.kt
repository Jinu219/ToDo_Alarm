package com.kt_study.todo_alarm.categories

import com.kt_study.todo_alarm.categories.contents.ContentItem
import com.kt_study.todo_alarm.db.CategoryEntity
import com.kt_study.todo_alarm.db.ContentEntity

interface CategoryFocusChangeListener {
    fun onFocusOut(categoryItem: CategoryItem)
    fun onContentFocusOut(contentItem: ContentItem)
}