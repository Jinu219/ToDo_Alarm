package com.kt_study.todo_alarm.categories

import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.databinding.CategoryItemRecyclerviewBinding

class CategoryViewHolder(val binding: CategoryItemRecyclerviewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val contentRecyclerView: RecyclerView = binding.rvContent

    init {

    }

    fun bind(data: CategoryItem) {

    }
}