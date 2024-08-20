package com.kt_study.todo_alarm.categories.contents

import androidx.room.parser.expansion.Position

interface ContentNotificationListener {
    fun onActiveAlarm(contentPosition: Int, isNotificationEnabled: Boolean)
}