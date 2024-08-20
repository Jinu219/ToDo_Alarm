package com.kt_study.todo_alarm.categories.contents

interface ContentAlarmBtnClickListener {
    fun onAlarmBtnClick(contentPosition: Int, ontTimeSet: (hour: Int, min: Int) -> Unit)
}