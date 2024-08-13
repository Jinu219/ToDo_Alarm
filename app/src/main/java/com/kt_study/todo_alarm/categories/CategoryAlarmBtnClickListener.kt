package com.kt_study.todo_alarm.categories

interface CategoryAlarmBtnClickListener {
    fun onContentClick(
        parentPosition: Int,
        childPosition: Int,
        currentHour: Int,
        currentMin: Int,
        updateTimeCallBack: (hour: Int, min: Int) -> Unit
    )
}