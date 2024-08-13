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
import kotlinx.coroutines.flow.firstOrNull
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
            contentId = nowContentId,
            categoryId = categoryId,
            toDo = toDo,
            hour = hour,
            min = min
        )

        viewModelScope.launch {
            // 현재 카테고리에 속한 기존 콘텐츠를 가져옵니다.
            val existingContents = withContext(Dispatchers.IO) {
                repository.getContentsByCategoryId(categoryId).firstOrNull() ?: listOf()
            }

            // 기존 콘텐츠를 ContentItem으로 변환하고 새 콘텐츠를 추가합니다.
            val updatedContents = existingContents.map { contentEntity ->
                ContentItem(
                    contentId = contentEntity.contentId,
                    categoryId = contentEntity.categoryId,
                    toDo = contentEntity.toDo,
                    hour = contentEntity.hour,
                    min = contentEntity.min
                )
            }.toMutableList().apply {
                add(newContent) // 새 콘텐츠를 리스트에 추가합니다.
            }

            // LiveData에서 현재 카테고리 목록을 가져옵니다.
            val categories = _categories.value?.toMutableList() ?: mutableListOf()

            // 새로운 콘텐츠를 추가할 특정 카테고리를 찾습니다.
            val category = categories.find { it.id == categoryId }

            // 특정 카테고리를 찾지 못하면 종료
            if (category == null) return@launch

            // 새로운 콘텐츠가 추가된 리스트로 콘텐츠를 업데이트합니다.
            category.contents = updatedContents

            // 수정된 카테고리 목록으로 LiveData를 업데이트합니다.
            _categories.value = categories

            // 새로운 콘텐츠를 데이터베이스에 삽입합니다.
            insertContent(
                ContentEntity(
                    contentId = newContent.contentId,
                    categoryId = categoryId,
                    toDo = newContent.toDo,
                    hour = newContent.hour,
                    min = newContent.min
                )
            )
        }
    }






    fun clearDatabase() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.clearDatabase()
        }
    }
}