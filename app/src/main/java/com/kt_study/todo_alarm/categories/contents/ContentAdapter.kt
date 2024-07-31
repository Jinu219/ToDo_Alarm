package com.kt_study.todo_alarm.categories.contents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.databinding.ItemContentBinding

class ContentAdapter(
    private val contents: MutableList<ContentItem>,
) :
    RecyclerView.Adapter<ContentViewHolder>() {
    private lateinit var alarmClickListener: ContentEventListener

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
    }

    fun setOnAlarmClickListener(listener: ContentEventListener) {
        alarmClickListener = listener
    }
}