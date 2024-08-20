package com.kt_study.todo_alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    private val application: ToDoApplication,
    private var repository: ToDoListRepository
) : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryItem>>(/* value = */ listOf())
    val categories: LiveData<List<CategoryItem>> get() = _categories

    init {
        // ViewModel 초기화 시 데이터 로드 및 변환
        viewModelScope.launch {
            // 데이터베이스에서 모든 카테고리와 콘텐츠를 가져옵니다.
            val categoriesFromDb = repository.getAllCategories.firstOrNull() ?: listOf()
            val contentsFromDb = repository.getAllContents.firstOrNull() ?: listOf()

            // 카테고리와 콘텐츠를 UI에 적합한 형식으로 변환합니다.
            val categoryItems = categoriesFromDb.map { categoryEntity ->
                // 해당 카테고리에 속하는 콘텐츠를 필터링하고 변환합니다.
                val contentItems = contentsFromDb.filter { it.categoryId == categoryEntity.id }
                    .map { contentEntity ->
                        ContentItem(
                            contentId = contentEntity.contentId,
                            categoryId = contentEntity.categoryId,
                            toDo = contentEntity.toDo,
                            hour = contentEntity.hour,
                            min = contentEntity.min,
                            isChecked = contentEntity.isChecked
                        )
                    }.toMutableList()

                CategoryItem(
                    id = categoryEntity.id,
                    title = categoryEntity.title,
                    contents = contentItems
                )
            }

            // 변환된 데이터를 LiveData에 설정합니다.
            _categories.value = categoryItems
        }
    }

    private suspend fun getNextCategoryId(): Int {
        val maxCategoryId = repository.getMaxCategoryId() ?: 0
        return maxCategoryId + 1
    }

    private suspend fun getNextContentId(): Int {
        val maxContentId = repository.getMaxContentId() ?: 0
        return maxContentId + 1
    }

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

    fun deleteContent(content: ContentEntity) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.deleteContent(content)
        }
    }

    // RecyclerView
    fun makeCategory(title: String) {
        viewModelScope.launch {
            val newCategoryId = getNextCategoryId()
            // 기존 카테고리 및 콘텐츠를 가져옵니다.
            val existingCategories = repository.getAllCategories.firstOrNull() ?: listOf()
            val existingContents = repository.getAllContents.firstOrNull() ?: listOf()

            // 새로운 카테고리를 생성합니다.
            val newCategory =
                CategoryItem(id = newCategoryId, title = title, contents = mutableListOf())

            // 기존 카테고리와 콘텐츠를 포함하여 업데이트된 카테고리 목록을 생성합니다.
            val updatedCategories = existingCategories.map { categoryEntity ->
                CategoryItem(
                    id = categoryEntity.id,
                    title = categoryEntity.title,
                    contents = existingContents.filter { it.categoryId == categoryEntity.id }
                        .map { contentEntity ->
                            ContentItem(
                                contentId = contentEntity.contentId,
                                categoryId = contentEntity.categoryId,
                                toDo = contentEntity.toDo,
                                hour = contentEntity.hour,
                                min = contentEntity.min,
                                isChecked = contentEntity.isChecked
                            )
                        }.toMutableList()
                )
            }.toMutableList()

            // 새로운 카테고리를 추가합니다.
            updatedCategories.add(newCategory)

            // LiveData를 업데이트하여 RecyclerView가 변경 사항을 반영하도록 합니다.
            _categories.value = updatedCategories

            // 새 카테고리를 데이터베이스에 삽입합니다.
            insertCategory(
                CategoryEntity(
                    id = newCategoryId,
                    title = title
                )
            )
        }
    }


    fun makeContent(
        categoryPosition: Int,
        categoryId: Int,
        toDo: String,
        hour: Int,
        min: Int,
        isChecked: Boolean
    ) {
        viewModelScope.launch {
            val newContentId = getNextContentId()

            // LiveData에서 현재 카테고리 목록을 가져옵니다.
            val categories = _categories.value?.toMutableList() ?: mutableListOf()

            // 수정된 카테고리 정보를 가져오기 위해 DB에서 최신 카테고리 정보를 가져옵니다.
            val updatedCategoryEntity = repository.getCategoryById(categoryId).firstOrNull()

            if (updatedCategoryEntity == null) return@launch // 카테고리를 찾지 못하면 종료

            // 카테고리 제목을 수정된 값으로 설정
            val category = categories.find { it.id == categoryId }
            category?.title = updatedCategoryEntity.title // 수정된 제목 반영

            // 새로운 콘텐츠를 추가합니다.
            val newContent = ContentItem(
                contentId = newContentId,
                categoryId = categoryId,
                toDo = toDo,
                hour = hour,
                min = min,
                isChecked = isChecked
            )

            category?.contents?.add(newContent)

            // 수정된 카테고리 목록으로 LiveData를 업데이트합니다.
            _categories.value = categories

            // 새로운 콘텐츠를 데이터베이스에 삽입합니다.
            insertContent(
                ContentEntity(
                    contentId = newContent.contentId,
                    categoryId = categoryId,
                    toDo = newContent.toDo,
                    hour = newContent.hour,
                    min = newContent.min,
                    isChecked = newContent.isChecked
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