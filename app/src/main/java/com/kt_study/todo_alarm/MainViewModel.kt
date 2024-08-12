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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repository: ToDoListRepository
) : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryItem>>(/* value = */ listOf())
    val categories: LiveData<List<CategoryItem>> get() = _categories
    private var nowCategoryId = 1
    private var nowContentId = 1

    val getAllCategories: LiveData<List<CategoryEntity>> = repository.getAllCategory.asLiveData()
    val getAllContents: LiveData<List<ContentEntity>> = repository.getAllContent.asLiveData()

    // DB Insert Value
    private fun insertCategory(category: CategoryEntity) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.insertCategory(category)
        }
    }

    private fun insertContent(content: ContentEntity) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.insertContent(content)
        }
    }

    // DB Update Value
    fun updateCategory(category: CategoryEntity) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.updateCategory(category)
        }
    }

    fun updateContent(content: ContentEntity) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.updateContent(content)
        }
    }


    // RecyclerView
    fun makeCategory(title: String) {
        nowCategoryId++
        val newCategory =
            CategoryItem(id = nowCategoryId, title = title, contents = mutableListOf())
        val categories = categories.value ?: listOf()
        _categories.value = categories + newCategory

        insertCategory(
            CategoryEntity(
                id = newCategory.id,
                title = newCategory.title
            )
        )

    }

    fun makeContent(categoryPosition: Int, categoryId: Int, toDo: String, hour: Int, min: Int) {
        nowContentId++
        val newContent = ContentItem(
            id = nowContentId,
            categoryId = categoryId,
            toDo = toDo,
            hour = hour,
            min = min
        )
        val category = _categories.value ?: listOf()
        category[categoryPosition].contents.add(newContent)

        insertContent(
            ContentEntity(
                id = newContent.id,
                categoryId = category[categoryPosition].id,
                toDo = newContent.toDo,
                hour = newContent.hour,
                min = newContent.min

            )
        )
    }

    fun clearDatabase() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.clearDatabase()
        }
    }
}