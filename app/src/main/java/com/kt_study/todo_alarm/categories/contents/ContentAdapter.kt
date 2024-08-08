package com.kt_study.todo_alarm.categories.contents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.categories.CategoryFocusChangeListener
import com.kt_study.todo_alarm.databinding.ItemContentBinding
import com.kt_study.todo_alarm.db.CategoryEntity
import com.kt_study.todo_alarm.db.ContentEntity

class ContentAdapter(
    private val contents: MutableList<ContentItem>,
) :
    RecyclerView.Adapter<ContentViewHolder>() {
    private lateinit var alarmClickListener: ContentEventListener
    private lateinit var contentFocusChangeListener: ContentFocusChangeListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding = ItemContentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContentViewHolder(binding)
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.binding.btnAlarm.setOnClickListener {
            alarmClickListener.onAlarmBtnClick(position)
        }
        holder.binding.etContent.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newValue = holder.binding.etContent.text.toString()
                val contentId = contents[position].id
                contentFocusChangeListener.onFocusOut(
                    ContentEntity(
                        id = contentId,
                        toDo = newValue
                    )
                )
            }
        }
    }

    fun setFocusChangeListener(listener: ContentFocusChangeListener) {
        contentFocusChangeListener = listener
    }
    fun setOnAlarmClickListener(listener: ContentEventListener) {
        alarmClickListener = listener
    }
}