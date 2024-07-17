package com.kt_study.todo_alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kt_study.todo_alarm.categories.CategoryItem
import com.kt_study.todo_alarm.categories.contents.ContentAdapter
import com.kt_study.todo_alarm.categories.contents.ContentItem

class MainViewModel() : ViewModel() {
    private val _categories = MutableLiveData<List<CategoryItem>>(/* value = */ listOf())
    private val _contents = MutableLiveData<List<ContentItem>>(/* value = */ listOf())
    val categories: LiveData<List<CategoryItem>> get() = _categories
    val contents: LiveData<List<ContentItem>> get() = _contents
    private var nowCategoryId = 0L
    private var nowContentId = 0L

    fun makeCategory(title: String) {
        nowCategoryId++
        val newCategory =
            CategoryItem(id = nowCategoryId, title = title, contents = mutableListOf())
        val categories = categories.value ?: listOf()
        _categories.value = categories + newCategory
    }

    fun makeContent(text: String, time: String) {
        nowContentId++
        val newContent = ContentItem(id = nowContentId, text = text, time = time)
        val contents = contents.value ?: listOf()
        _contents.value = contents + newContent
    }
}