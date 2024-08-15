package com.kt_study.todo_alarm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kt_study.todo_alarm.categories.CategoryAdapter
import com.kt_study.todo_alarm.categories.CategoryAlarmBtnClickListener
import com.kt_study.todo_alarm.categories.CategoryCheckBoxChangeListener
import com.kt_study.todo_alarm.categories.CategoryContentDeleteListener
import com.kt_study.todo_alarm.categories.CategoryFocusChangeListener
import com.kt_study.todo_alarm.categories.CategoryItem
import com.kt_study.todo_alarm.categories.CategoryTextChangeListener
import com.kt_study.todo_alarm.categories.contents.ContentItem
import com.kt_study.todo_alarm.databinding.ActivityMainBinding
import com.kt_study.todo_alarm.db.CategoryEntity
import com.kt_study.todo_alarm.db.ContentEntity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private var isAlarmSetting = false
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            application as ToDoApplication,
            (application as ToDoApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserve()
        initBtn()

    }


    private fun initObserve() {
        viewModel.categories.observe(this) { categories ->
            categoryAdapter = CategoryAdapter(
                this,
                categories.toMutableList()
            ) { position ->
                val categoryId = categories[position].id
                viewModel.makeContent(position, categoryId, "", 0, 0, false)
            }

            categoryAdapter.setCategoryContentDeleteListener(object :
                CategoryContentDeleteListener {
                override fun onContentDelete(categoryPosition: Int, contentItem: ContentItem) {
                    val contentEntity = convertToContentEntity(contentItem)
                    viewModel.deleteContent(contentEntity)
                }
            })

            categoryAdapter.setTextChangeListener(object : CategoryTextChangeListener {
                override fun onContentTextChange(
                    categoryPosition: Int,
                    contentPosition: Int,
                    toDo: String
                ) {
                    val categoryId = categories[categoryPosition].id
                    val contentId = categories[categoryPosition].contents[contentPosition].contentId
                    val contentItem = categoryAdapter.getContentItem(
                        categoryPosition = categoryPosition,
                        contentPosition = contentPosition
                    )
                    val contentEntity = convertToContentEntity(contentItem)
                    val updatedContentEntity = contentEntity.copy(
                        contentId = contentId,
                        categoryId = categoryId,
                        toDo = toDo,
                        hour = contentEntity.hour,
                        min = contentEntity.min,
                        isChecked = contentEntity.isChecked

                    )
                    viewModel.updateContent(updatedContentEntity)
                }

                override fun onTextChange(categoryPosition: Int, title: String) {
                    val categoryId = categories[categoryPosition].id
                    val categoryItem = categories[categoryPosition]
                    val categoryEntity = convertToCategoryEntity(categoryItem)
                    val updatedCategoryEntity = categoryEntity.copy(
                        id = categoryId,
                        title = title
                    )
                    viewModel.updateCategory(updatedCategoryEntity)
                }
            })

            categoryAdapter.setCheckBoxChangeListener(object : CategoryCheckBoxChangeListener {
                override fun onCheckBoxChanged(
                    categoryPosition: Int,
                    contentPosition: Int,
                    isChecked: Boolean
                ) {
                    val contentItem =
                        categoryAdapter.getContentItem(categoryPosition, contentPosition)
                    val contentEntity = convertToContentEntity(contentItem)
                    val categoryId = categories[categoryPosition].id
                    val contentId = contentItem.contentId
                    val updatedEntity = contentEntity.copy(
                        categoryId = categoryId,
                        contentId = contentId,
                        isChecked = isChecked
                    )
                    viewModel.updateContent(updatedEntity)
                }
            })

            categoryAdapter.setContentClickListener(object : CategoryAlarmBtnClickListener {
                override fun onContentClick(
                    parentPosition: Int,
                    childPosition: Int,
                    currentHour: Int,
                    currentMin: Int,
                    updateTimeCallBack: (hour: Int, min: Int) -> Unit
                ) {
                    isAlarmSetting = true
                    val contentItem = categoryAdapter.getContentItem(parentPosition, childPosition)
                    val contentEntity = convertToContentEntity(contentItem)
                    val alarmFragment = AlarmFragment(
                        initialHour = contentItem.hour,
                        initialMin = contentItem.min
                    ) { selectedHour, selectedMin ->
                        val contentId = categories[parentPosition].contents[childPosition].contentId
                        val updatedContentEntity =
                            contentEntity.copy(
                                contentId = contentId,
                                toDo = contentItem.toDo,
                                hour = selectedHour,
                                min = selectedMin,
                                isChecked = contentItem.isChecked
                            )
                        viewModel.updateContent(updatedContentEntity)
                        updateTimeCallBack(selectedHour, selectedMin)
                        isAlarmSetting = false
                    }
                    alarmFragment.show(supportFragmentManager, "alarmFragment")
                }
            })
            categoryAdapter.setFocusChangeListener(
                object : CategoryFocusChangeListener {
                    override fun onFocusOut(categoryItem: CategoryItem) {
                        val updateCategoryEntity = convertToCategoryEntity(categoryItem)
                        viewModel.updateCategory(updateCategoryEntity)
                    }

                    override fun onContentFocusOut(contentItem: ContentItem) {
                        val updateContentEntity = convertToContentEntity(contentItem)
                        viewModel.updateContent(updateContentEntity)
                    }
                }
            )

            binding.rvCategory.adapter = categoryAdapter
        }

    }

    private fun initBtn() {
        binding.btnAddCategory.setOnClickListener {
            viewModel.makeCategory("")
        }
    }

    fun convertToCategoryEntity(categoryItem: CategoryItem): CategoryEntity = CategoryEntity(
        id = categoryItem.id,
        title = categoryItem.title
    )

    fun convertToContentEntity(contentItem: ContentItem): ContentEntity = ContentEntity(
        contentId = contentItem.contentId,
        categoryId = contentItem.categoryId,
        toDo = contentItem.toDo,
        hour = contentItem.hour,
        min = contentItem.min,
        isChecked = contentItem.isChecked
    )

    fun convertToContentItem(contentEntity: ContentEntity): ContentItem = ContentItem(
        contentId = contentEntity.contentId,
        categoryId = contentEntity.categoryId,
        toDo = contentEntity.toDo,
        hour = contentEntity.hour,
        min = contentEntity.min,
        isChecked = contentEntity.isChecked
    )

}