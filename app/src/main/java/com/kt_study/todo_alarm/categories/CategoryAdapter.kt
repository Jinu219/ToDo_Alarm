package com.kt_study.todo_alarm.categories

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.R
import com.kt_study.todo_alarm.categories.contents.ContentAdapter
import com.kt_study.todo_alarm.categories.contents.ContentAlarmBtnClickListener
import com.kt_study.todo_alarm.categories.contents.ContentCheckBoxChangeListener
import com.kt_study.todo_alarm.categories.contents.ContentDeleteListener
import com.kt_study.todo_alarm.categories.contents.ContentFocusChangeListener
import com.kt_study.todo_alarm.categories.contents.ContentItem
import com.kt_study.todo_alarm.categories.contents.ContentTextChangeListener
import com.kt_study.todo_alarm.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val context: Context,
    private val categories: MutableList<CategoryItem>,
    private val makeContentItems: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<CategoryViewHolder>() {
    private lateinit var contentClickListener: CategoryAlarmBtnClickListener
    private lateinit var categoryFocusChangeListener: CategoryFocusChangeListener
    private lateinit var categoryTextChangeListener: CategoryTextChangeListener
    private lateinit var categoryCheckBoxChangeListener: CategoryCheckBoxChangeListener
    private lateinit var categoryContentDeleteListener: CategoryContentDeleteListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.bind(categories[position])
        val item = categories[position]
        holder.binding.etCategory.text =
            SpannableStringBuilder(context.getString(R.string.to_do_title, item.title))

        val contentAdapter = ContentAdapter(holder.itemView.context, categories[position].contents)
        holder.contentRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.contentRecyclerView.adapter = contentAdapter

        contentAdapter.setCheckBoxChangeListener(object : ContentCheckBoxChangeListener{
            override fun onCheckBoxChanged(contentItem: ContentItem) {
                categoryCheckBoxChangeListener.onCheckBoxChanged(
                    categoryPosition = position,
                    contentPosition = contentItem.contentId,
                    isChecked = contentItem.isChecked
                )
            }
        })

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
            override fun onFocusOut(contentItem: ContentItem) {
                categoryFocusChangeListener.onContentFocusOut(
                    ContentItem(
                        contentId = contentItem.contentId,
                        categoryId = categories[holder.adapterPosition].id,
                        toDo = contentItem.toDo,
                        hour = contentItem.hour,
                        min = contentItem.min,
                        isChecked = contentItem.isChecked
                    )
                )
            }
        })

        contentAdapter.setTextChangeListener(object : ContentTextChangeListener {
            override fun onTextChange(position: Int, toDo: String) {
                categoryTextChangeListener.onContentTextChange(
                    holder.adapterPosition,
                    position,
                    toDo
                )
            }
        })

        val itemTouchHelper = ItemTouchHelper(contentAdapter.itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(holder.contentRecyclerView)

        contentAdapter.setContentDeleteListener(object : ContentDeleteListener {
            override fun onContentDelete(contentItem: ContentItem) {
                categoryContentDeleteListener.onContentDelete(position, contentItem)
            }
        })

        holder.binding.btnAddContent.setOnClickListener {
            makeContentItems(position)
        }


        // etCategory의 값이 엔터를 눌렀을떄에 DB로 값이 업데이트가 되게 구현
        holder.binding.etCategory.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newValue = holder.binding.etCategory.text.toString()
                val categoryId = categories[position].id
                categoryFocusChangeListener.onFocusOut(
                    CategoryItem(
                        id = categoryId,
                        title = newValue
                    )
                )
            }
        }

        holder.binding.etCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    categoryTextChangeListener.onTextChange(holder.adapterPosition, s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun getContentItem(categoryPosition: Int, contentPosition: Int): ContentItem {
        return categories[categoryPosition].contents[contentPosition]
    }


    fun setCheckBoxChangeListener(listener: CategoryCheckBoxChangeListener){
        categoryCheckBoxChangeListener = listener
    }
    fun setFocusChangeListener(listener: CategoryFocusChangeListener) {
        categoryFocusChangeListener = listener
    }

    fun setTextChangeListener(listener: CategoryTextChangeListener) {
        categoryTextChangeListener = listener
    }

    fun setContentClickListener(listener: CategoryAlarmBtnClickListener) {
        contentClickListener = listener
    }
    fun setCategoryContentDeleteListener(listener: CategoryContentDeleteListener) {
        categoryContentDeleteListener = listener
    }
}