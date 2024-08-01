package com.kt_study.todo_alarm.categories.contents

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.ToDoListDatabase
import com.kt_study.todo_alarm.databinding.ItemContentBinding

class ContentAdapter(
    private val contents: MutableList<Content>,
) :
    RecyclerView.Adapter<ContentViewHolder>() {
    private lateinit var alarmClickListener: ContentEventListener
    private lateinit var db: ToDoListDatabase

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
        holder.binding.etContent.setOnFocusChangeListener(object : OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    Log.d("editText", "It Has Focus!")
                    db.contentDao().insertContent(
                        Content(toDo = holder.binding.etContent.text.toString())
                    )
                }

            }
        })
    }

    fun setOnAlarmClickListener(listener: ContentEventListener) {
        alarmClickListener = listener
    }
}