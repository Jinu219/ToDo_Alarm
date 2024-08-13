package com.kt_study.todo_alarm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kt_study.todo_alarm.categories.CategoryAdapter
import com.kt_study.todo_alarm.categories.CategoryAlarmBtnClickListener
import com.kt_study.todo_alarm.categories.CategoryFocusChangeListener
import com.kt_study.todo_alarm.categories.CategoryTextChangeListener
import com.kt_study.todo_alarm.categories.contents.ContentItem
import com.kt_study.todo_alarm.databinding.ActivityMainBinding
import com.kt_study.todo_alarm.db.CategoryEntity
import com.kt_study.todo_alarm.db.ContentEntity
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel: MainViewModel by viewModels() {
        MainViewModelFactory((application as ToDoApplication).repository)
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
        lifecycleScope.launch {
            viewModel.clearDatabase()
        }
        viewModel.categories.observe(this) { categories ->
            categoryAdapter = CategoryAdapter(this, categories.toMutableList()) { position ->
                val categoryId = categories[position].id
                viewModel.makeContent(position, categoryId, "", 0, 0)

            }

            categoryAdapter.setTextChangeListener(object : CategoryTextChangeListener {
                override fun onContentTextChange(
                    categoryPosition: Int,
                    contentPosition: Int,
                    toDo: String
                ) {
                    val categoryId = categories[categoryPosition].id
                    val contentId = categories[categoryPosition].contents[contentPosition].contentId
                    val contentItem = categoryAdapter.getContentItem(categoryPosition = categoryPosition, contentPosition = contentPosition)
                    val contentEntity = convertToContentEntity(contentItem)
                    val updatedContentEntity = contentEntity.copy(
                        contentId = contentId,
                        categoryId = categoryId,
                        toDo = toDo
                    )
                    viewModel.updateContent(updatedContentEntity)
//                    categoryAdapter.notifyItemChanged(categoryPosition)
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
                                hour = selectedHour,
                                min = selectedMin,
                                toDo = contentEntity.toDo
                            )
                        viewModel.updateContent(updatedContentEntity)
//                        categoryAdapter.notifyItemChanged(parentPosition)
                        updateTimeCallBack(selectedHour, selectedMin)
                    }
                    alarmFragment.show(supportFragmentManager, "alarmFragment")
                }
            })
            categoryAdapter.setFocusChangeListener(
                object : CategoryFocusChangeListener {
                    override fun onFocusOut(categoryEntity: CategoryEntity) {
                        viewModel.updateCategory(categoryEntity)
                    }

                    override fun onContentFocusOut(contentEntity: ContentEntity) {
                        viewModel.updateContent(contentEntity)
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

    fun convertToContentEntity(contentItem: ContentItem): ContentEntity = ContentEntity(
        contentId = contentItem.contentId,
        categoryId = contentItem.categoryId,
        toDo = contentItem.toDo,
        hour = contentItem.hour,
        min = contentItem.min
    )

    fun convertToContentItem(contentEntity: ContentEntity): ContentItem = ContentItem(
        contentId = contentEntity.contentId,
        categoryId = contentEntity.categoryId,
        toDo = contentEntity.toDo,
        hour = contentEntity.hour,
        min = contentEntity.min
    )

}