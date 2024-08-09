package com.kt_study.todo_alarm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kt_study.todo_alarm.categories.CategoryAdapter
import com.kt_study.todo_alarm.categories.CategoryEventListener
import com.kt_study.todo_alarm.categories.CategoryFocusChangeListener
import com.kt_study.todo_alarm.categories.CategoryItem
import com.kt_study.todo_alarm.categories.contents.ContentItem
import com.kt_study.todo_alarm.databinding.ActivityMainBinding
import com.kt_study.todo_alarm.db.CategoryEntity
import com.kt_study.todo_alarm.db.ContentEntity


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

    fun initObserve() {
        viewModel.categories.observe(this) { categories ->
            categoryAdapter = CategoryAdapter(categories.toMutableList()) { position ->
                val categoryId = categories[position].id
                viewModel.makeContent(position, categoryId, "", 0, 0)
            }
            categoryAdapter.setContentClickListener(object : CategoryEventListener {
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
                        val updatedContentEntity =
                            contentEntity.copy(hour = selectedHour, min = selectedMin)
                        viewModel.updateContent(updatedContentEntity)
                        categoryAdapter.notifyItemChanged(parentPosition)
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
                })

            binding.rvCategory.adapter = categoryAdapter
        }

    }

    fun initBtn() {
        binding.btnAddCategory.setOnClickListener {
            viewModel.makeCategory("")
        }
    }

    fun convertToContentEntity(contentItem: ContentItem): ContentEntity = ContentEntity(
        id = contentItem.id,
        categoryId = contentItem.categoryId,
        toDo = contentItem.toDo,
        hour = contentItem.hour,
        min = contentItem.min
    )

    fun convertToContentItem(contentEntity: ContentEntity): ContentItem = ContentItem(
        id = contentEntity.id,
        categoryId = contentEntity.categoryId,
        toDo = contentEntity.toDo,
        hour = contentEntity.hour,
        min = contentEntity.min
    )

}