package com.kt_study.todo_alarm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kt_study.todo_alarm.categories.CategoryAdapter
import com.kt_study.todo_alarm.categories.CategoryEventListener
import com.kt_study.todo_alarm.categories.CategoryFocusChangeListener
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
        viewModel.categories.observe(this) {
            categoryAdapter = CategoryAdapter(it.toMutableList()) { position ->
                viewModel.makeContent(position, "", 0, 0)
            }
            categoryAdapter.setContentClickListener(object : CategoryEventListener {
                override fun onContentClick(
                    parentPosition: Int,
                    childPosition: Int,
                    updateTimeCallBack: (hour: Int, min: Int) -> Unit
                ) {
                    val alarmFragment = AlarmFragment { selectedHour, selectedMin ->
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
}