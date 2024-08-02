package com.kt_study.todo_alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kt_study.todo_alarm.categories.Category
import com.kt_study.todo_alarm.db.ToDoListDatabase
import com.kt_study.todo_alarm.db.ToDoListRepository
import com.kt_study.todo_alarm.categories.contents.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application){
    private val _categories = MutableLiveData<List<Category>>(listOf())
    private val repository: ToDoListRepository = ToDoListRepository.getInstance(application)
    private val getAllContent : LiveData<List<Content>>
    private val getAllCategory : LiveData<List<Category>>
    val categories: LiveData<List<Category>> get() = _categories

    private var nowCategoryId = 0L
    private var nowContentId = 0L

    init{
        getAllContent = repository.getAllContent
        getAllCategory = repository.getAllCategory
    }

    // DB
    fun addContent(content: Content){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addContent(content)
        }
    }

    fun addCategory(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCategory(category)
        }
    }



    // Add
    fun makeCategory(title: String) {
        nowCategoryId++
        val newCategory =
            Category(id = nowCategoryId, title = title, contents = mutableListOf())
        val categories = categories.value ?: listOf()
        _categories.value = categories + newCategory
    }

    fun makeContent(position: Int, text: String, hour: Int, min: Int) {
        nowContentId++
        val newContent = Content(
            id = nowContentId,
            categoryId = position.toLong(),
            toDo = text,
            hour = hour,
            min = min
        )
        val category = _categories.value ?: listOf()

        category[position].contents.add(newContent)
    }
}