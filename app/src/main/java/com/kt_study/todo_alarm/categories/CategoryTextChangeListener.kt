package com.kt_study.todo_alarm.categories

import com.kt_study.todo_alarm.categories.contents.ContentItem

interface CategoryTextChangeListener {
    fun onTextChange(categoryPosition: Int, title: String)
    fun onContentTextChange(categoryPosition: Int, contentPosition: Int, toDo: String)
}