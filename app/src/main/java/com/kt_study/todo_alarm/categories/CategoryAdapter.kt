package com.kt_study.todo_alarm.categories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.categories.contents.ContentAdapter
import com.kt_study.todo_alarm.categories.contents.ContentAlarmBtnClickListener
import com.kt_study.todo_alarm.categories.contents.ContentFocusChangeListener
import com.kt_study.todo_alarm.categories.contents.ContentItem
import com.kt_study.todo_alarm.databinding.ItemCategoryBinding
import com.kt_study.todo_alarm.db.CategoryEntity
import com.kt_study.todo_alarm.db.ContentEntity

class CategoryAdapter(
    private val categories: MutableList<CategoryItem>,
    private val makeContentItems: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<CategoryViewHolder>() {
    private lateinit var contentClickListener: CategoryAlarmBtnClickListener
    private lateinit var categoryFocusChangeListener: CategoryFocusChangeListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(categories[position])

        val contentAdapter = ContentAdapter(holder.itemView.context, categories[position].contents)
        holder.contentRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.contentRecyclerView.adapter = contentAdapter

        contentAdapter.setOnAlarmClickListener(object : ContentAlarmBtnClickListener {
            override fun onAlarmBtnClick(
                contentPosition: Int,
                ontTimeSet: (hour: Int, min: Int) -> Unit
            ) {
                val currentContent = categories[position].contents[contentPosition]
                contentClickListener.onContentClick(
                    holder.adapterPosition,
                    childPosition = contentPosition,
                    currentHour = currentContent.hour,
                    currentMin = currentContent.min
                ) { selectedHour, selectedMin ->
                    ontTimeSet(selectedHour, selectedMin)
                }
            }
        })

        contentAdapter.setFocusChangeListener(object : ContentFocusChangeListener {
            override fun onFocusOut(contentEntity: ContentEntity) {
                categoryFocusChangeListener.onContentFocusOut(
                    ContentEntity(
                        id = contentEntity.id,
                        categoryId = categories[holder.adapterPosition].id,
                        toDo = contentEntity.toDo,
                        hour = contentEntity.hour,
                        min = contentEntity.min
                    )
                )
            }
        })

        holder.binding.btnAddContent.setOnClickListener {
            makeContentItems(position)
            notifyItemChanged(position)
        }

        // etCategory의 값이 엔터를 눌렀을떄에 DB로 값이 업데이트가 되게 구현
        holder.binding.etCategory.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newValue = holder.binding.etCategory.text.toString()
                val categoryId = categories[position].id
                categoryFocusChangeListener.onFocusOut(
                    CategoryEntity(
                        id = categoryId,
                        title = newValue
                    )
                )
            }
        }
    }

    fun getContentItem(parentPosition: Int, childPosition: Int): ContentItem {
        return categories[parentPosition].contents[childPosition]
    }


    fun setFocusChangeListener(listener: CategoryFocusChangeListener) {
        categoryFocusChangeListener = listener
    }

    fun setContentClickListener(listener: CategoryAlarmBtnClickListener) {
        contentClickListener = listener
    }
}