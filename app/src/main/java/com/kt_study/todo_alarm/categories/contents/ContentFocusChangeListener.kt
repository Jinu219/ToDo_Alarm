package com.kt_study.todo_alarm.categories.contents

import com.kt_study.todo_alarm.db.ContentEntity

interface ContentFocusChangeListener {
    fun onFocusOut(contentEntity: ContentEntity)
}