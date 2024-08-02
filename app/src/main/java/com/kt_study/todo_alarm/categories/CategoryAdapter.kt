package com.kt_study.todo_alarm.categories

import android.icu.text.CaseMap.Title
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.categories.contents.Content
import com.kt_study.todo_alarm.categories.contents.ContentAdapter
import com.kt_study.todo_alarm.categories.contents.ContentEventListener
import com.kt_study.todo_alarm.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: MutableList<Category>,
    private val makeContentItems: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<CategoryViewHolder>() {
    private lateinit var categoryEventListener: CategoryEventListener
    private lateinit var categoryFocusListener: CategoryFocusListener

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

        val contentAdapter = ContentAdapter(categories[position].contents)
        holder.contentRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.contentRecyclerView.adapter = contentAdapter

        contentAdapter.setOnAlarmClickListener(object : ContentEventListener {
            override fun onAlarmBtnClick(contentPosition: Int) {
                categoryEventListener.onContentClick(holder.adapterPosition, contentPosition)
            }
        })

        holder.binding.btnAddContent.setOnClickListener {
            makeContentItems(position)
            notifyItemChanged(position)
        }

        holder.binding.etCategory.setOnFocusChangeListener(object : OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!hasFocus) {
                    Log.d("CategoryEditText", "It Has Focus!")
                    categoryFocusListener.addCategoryEntity(Category(title = holder.binding.etCategory.text.toString()))
                }
            }
        })
    }

    fun setCategoryListener(listener: CategoryEventListener) {
        categoryEventListener = listener
    }

    fun setFocusListener(listener: CategoryFocusListener) {
        categoryFocusListener = listener
    }
}