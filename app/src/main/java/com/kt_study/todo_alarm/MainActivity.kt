package com.kt_study.todo_alarm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kt_study.todo_alarm.alarm.AlarmFragment
import com.kt_study.todo_alarm.categories.Category
import com.kt_study.todo_alarm.categories.CategoryAdapter
import com.kt_study.todo_alarm.categories.CategoryEventListener
import com.kt_study.todo_alarm.categories.CategoryFocusListener
import com.kt_study.todo_alarm.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(application)
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
        viewModel.categories.observe(this) {
            categoryAdapter = CategoryAdapter(it.toMutableList()) { position ->
                viewModel.makeContent(position, "", 0,0)
            }
            categoryAdapter.setCategoryListener(object : CategoryEventListener {
                override fun onContentClick(parentPosition: Int, childPosition: Int) {
                    AlarmFragment().show(supportFragmentManager, AlarmFragment().tag)
                }
            })
            categoryAdapter.setFocusListener(object : CategoryFocusListener{
                override fun addCategoryEntity(category: Category) {
                    viewModel.addCategory(category)
                }
            })
            binding.rvCategory.adapter = categoryAdapter


        }

    }

    private fun initBtn() {
        binding.btnAddCategory.setOnClickListener {
            viewModel.makeCategory("")
        }
    }
}