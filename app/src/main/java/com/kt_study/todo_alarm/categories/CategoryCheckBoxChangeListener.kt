package com.kt_study.todo_alarm.categories

interface CategoryCheckBoxChangeListener {
    fun onCheckBoxChanged(categoryPosition:Int, contentPosition: Int, isChecked: Boolean)
}
