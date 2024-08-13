package com.kt_study.todo_alarm.categories

interface CategoryTextChangeListener {
    fun onTextChange(categoryPosition: Int, title:String)
    fun onContentTextChange(categoryPosition:Int, contentPosition:Int, toDo:String)
}