package com.kt_study.todo_alarm.categories

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.MainViewModel
import com.kt_study.todo_alarm.categories.contents.ContentAdapter
import com.kt_study.todo_alarm.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: MutableList<CategoryItem>,
    private val makeContentItems: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])

        var contentAdapter = ContentAdapter(categories[position].contents)
        holder.contentRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.contentRecyclerView.adapter = contentAdapter
        holder.binding.btnAddContent.setOnClickListener {
            Log.d("Adapter", "$position viewHolder Clicked!")
            makeContentItems(position)
            notifyItemChanged(position)
        }
    }

    fun getItem(position: Int): CategoryItem {
        return categories[position]
    }
}