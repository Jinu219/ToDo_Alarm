package com.kt_study.todo_alarm.categories

interface CategoryTextChangeListener {
    fun onContentTextChange(categoryPosition:Int, contentPosition:Int, toDo:String)
}