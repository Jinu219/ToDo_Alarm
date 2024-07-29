package com.kt_study.todo_alarm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kt_study.todo_alarm.categories.CategoryItem
import com.kt_study.todo_alarm.categories.contents.ContentAdapter
import com.kt_study.todo_alarm.categories.contents.ContentItem

class MainViewModel() : ViewModel() {
    private val _categories = MutableLiveData<List<CategoryItem>>(/* value = */ listOf())
    val categories: LiveData<List<CategoryItem>> get() = _categories
    private var nowCategoryId = 0L
    private var nowContentId = 0L

    fun makeCategory(title: String) {
        nowCategoryId++
        val newCategory =
            CategoryItem(id = nowCategoryId, title = title, contents = mutableListOf())
        val categories = categories.value ?: listOf()
        _categories.value = categories + newCategory
    }

    fun makeContent(position: Int, text: String, time: String) {
        nowContentId++
        val newContent = ContentItem(id = nowContentId, text = text, time = time)
        val category = _categories.value ?: listOf()
        val oldContents = category[position].contents
        category[position].contents.add(newContent)
    }
}