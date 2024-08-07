package com.kt_study.todo_alarm.categories

import com.kt_study.todo_alarm.db.CategoryEntity
import com.kt_study.todo_alarm.db.ContentEntity

interface CategoryFocusChangeListener {
    fun onFocusOut(categoryEntity: CategoryEntity)
    fun onContentFocusOut(contentEntity: ContentEntity)
}