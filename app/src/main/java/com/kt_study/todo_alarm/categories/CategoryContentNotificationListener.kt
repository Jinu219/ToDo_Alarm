package com.kt_study.todo_alarm.categories


interface CategoryContentNotificationListener {
    fun categoryContentNotificationListener(categoryPosition:Int, contentPosition: Int, isNotificationEnabled: Boolean)
}