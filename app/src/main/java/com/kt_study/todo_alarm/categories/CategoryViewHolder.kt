package com.kt_study.todo_alarm.categories

import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.MainViewModel
import com.kt_study.todo_alarm.databinding.ItemCategoryBinding

class CategoryViewHolder(val binding: ItemCategoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val contentRecyclerView: RecyclerView = binding.rvContent

    fun bind(data: CategoryItem) {

    }
}