package com.kt_study.todo_alarm.categories.contents

import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.databinding.ItemContentBinding

class ContentViewHolder(
    val binding: ItemContentBinding,
) : RecyclerView.ViewHolder(binding.root) {
    var textWatcher: TextWatcher? = null
    fun bind() {}
}