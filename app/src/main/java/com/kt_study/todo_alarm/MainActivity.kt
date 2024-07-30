package com.kt_study.todo_alarm

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kt_study.todo_alarm.categories.CategoryAdapter
import com.kt_study.todo_alarm.categories.CategoryViewHolder
import com.kt_study.todo_alarm.categories.contents.ContentAdapter
import com.kt_study.todo_alarm.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter

    private val viewModel: MainViewModel by viewModels()

    //val categories = arrayListOf(CategoryItem())
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
                viewModel.makeContent(position, "", "")
            }
            Log.d("makeContent", "init")
            binding.rvCategory.adapter = categoryAdapter
        }



    }

    fun initBtn() {
        binding.btnAddCategory.setOnClickListener {
            AlarmFragment().show(supportFragmentManager, AlarmFragment().tag)
            viewModel.makeCategory("")
        }


    }

}