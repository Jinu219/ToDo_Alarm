package com.kt_study.todo_alarm.categories.contents

import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.databinding.ItemContentBinding

class ContentViewHolder(
    val binding: ItemContentBinding,
    private val alarmClickListener: ContentEventListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
    }
}