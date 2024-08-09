package com.kt_study.todo_alarm.categories.contents

data class ContentItem(
    val id: Int,
    val text: String = "",
    var hour: Int = 0,
    var min: Int = 0
)