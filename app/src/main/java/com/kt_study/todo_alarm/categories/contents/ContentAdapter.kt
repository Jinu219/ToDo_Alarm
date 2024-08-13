package com.kt_study.todo_alarm.categories.contents

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.R
import com.kt_study.todo_alarm.databinding.ItemContentBinding
import com.kt_study.todo_alarm.db.ContentEntity

class ContentAdapter(
    private val context: Context,
    private val contents: MutableList<ContentItem>,
) :
    RecyclerView.Adapter<ContentViewHolder>() {
    private lateinit var alarmClickListener: ContentAlarmBtnClickListener
    private lateinit var contentFocusChangeListener: ContentFocusChangeListener
    private lateinit var textChangeListener: ContentTextChangeListener

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
        val item = contents[position]

        holder.binding.tvAlarmTime.text =
            context.getString(R.string.to_do_time, item.hour, item.min)

        holder.binding.btnAlarm.setOnClickListener {
            alarmClickListener.onAlarmBtnClick(position) { hour, min ->
                item.hour = hour
                item.min = min
                notifyItemChanged(position)
            }
        }

        holder.binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    textChangeListener.onTextChange(holder.adapterPosition, s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        holder.binding.etContent.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newValue = holder.binding.etContent.text.toString()
                val contentId = contents[position].id
                contentFocusChangeListener.onFocusOut(
                    ContentEntity(
                        id = contentId,
                        toDo = newValue,
                        hour = item.hour,
                        min = item.min
                    )
                )
            }
        }
    }

    fun setFocusChangeListener(listener: ContentFocusChangeListener) {
        contentFocusChangeListener = listener
    }

    fun setTextChangeListener(listener: ContentTextChangeListener) {
        textChangeListener = listener
    }

    fun setOnAlarmClickListener(listener: ContentAlarmBtnClickListener) {
        alarmClickListener = listener
    }
}