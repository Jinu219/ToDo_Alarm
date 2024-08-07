package com.kt_study.todo_alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kt_study.todo_alarm.categories.CategoryItem
import com.kt_study.todo_alarm.categories.contents.ContentItem
import com.kt_study.todo_alarm.db.CategoryEntity
import com.kt_study.todo_alarm.db.ContentEntity
import com.kt_study.todo_alarm.db.ToDoListRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ToDoListRepository
) : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryItem>>(/* value = */ listOf())
    val categories: LiveData<List<CategoryItem>> get() = _categories
    private var nowCategoryId = 0L
    private var nowContentId = 0L

    val getAllCategories: LiveData<List<CategoryEntity>> = repository.getAllCategory.asLiveData()
    val getAllContents: LiveData<List<ContentEntity>> = repository.getAllContent.asLiveData()

    // DB
    fun insertCategory(category: CategoryEntity) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    fun insertContent(content: ContentEntity) = viewModelScope.launch {
        repository.insertContent(content)
    }


    // RecyclerView
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
        category[position].contents.add(newContent)
    }
}