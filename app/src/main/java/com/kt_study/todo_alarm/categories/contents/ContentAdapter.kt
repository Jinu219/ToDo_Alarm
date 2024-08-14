package com.kt_study.todo_alarm.categories.contents

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.R
import com.kt_study.todo_alarm.databinding.ItemContentBinding

class ContentAdapter(
    private val context: Context,
    private val contents: MutableList<ContentItem>,
) :
    RecyclerView.Adapter<ContentViewHolder>() {
    private lateinit var alarmClickListener: ContentAlarmBtnClickListener
    private lateinit var contentFocusChangeListener: ContentFocusChangeListener
    private lateinit var textChangeListener: ContentTextChangeListener
    private lateinit var checkBoxChangeListener: ContentCheckBoxChangeListener
    private lateinit var contentDeleteListener: ContentDeleteListener

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

        holder.binding.etContent.text =
            SpannableStringBuilder(context.getString(R.string.to_do, item.toDo))
        updateEditTextStyle(
            holder.binding.etContent,
            item.isChecked,
            holder.binding.etContent.text.toString()
        )
        holder.binding.cbCheck.isChecked = item.isChecked

        holder.binding.cbCheck.setOnCheckedChangeListener { _, isChecked ->
            val currentText = holder.binding.etContent.text.toString()
            item.toDo = currentText
            item.isChecked = isChecked
            checkBoxChangeListener.onCheckBoxChanged(
                ContentItem(
                    contentId = position,
                    categoryId = item.categoryId,
                    toDo = item.toDo,
                    hour = item.hour,
                    min = item.min,
                    isChecked = item.isChecked
                )
            )
            updateEditTextStyle(
                holder.binding.etContent,
                item.isChecked,
                holder.binding.etContent.text.toString()
            )
        }


        holder.binding.btnAlarm.setOnClickListener {
            val currentText = holder.binding.etContent.text.toString()
            item.toDo = currentText

            alarmClickListener.onAlarmBtnClick(position) { hour, min ->
                item.hour = hour
                item.min = min
                holder.binding.tvAlarmTime.text =
                    context.getString(R.string.to_do_time, item.hour, item.min)
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
                val contentId = contents[position].contentId
                contentFocusChangeListener.onFocusOut(
                    ContentItem(
                        contentId = contentId,
                        categoryId = item.categoryId,
                        toDo = newValue,
                        hour = item.hour,
                        min = item.min,
                        isChecked = item.isChecked
                    )
                )
            }
        }


    }

    val itemTouchHelperCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> deleteItem(position)
                    ItemTouchHelper.RIGHT -> Log.d("SwipeRight","Alarm")
                }
            }
        }
    fun deleteItem(position: Int) {
        if (position < contents.size) {
            val deletedItem = contents[position]
            contents.removeAt(position)
            notifyItemRemoved(position)
            contentDeleteListener.onContentDelete(deletedItem)
        }
    }

    private fun updateEditTextStyle(editText: EditText, isChecked: Boolean, text: String) {
        val spannable = SpannableStringBuilder(text)
        if (isChecked) {
            val length = spannable.length
            spannable.setSpan(StrikethroughSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(
                ForegroundColorSpan(Color.GRAY),
                0,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            val strikethroughSpans =
                spannable.getSpans(0, spannable.length, StrikethroughSpan::class.java)
            for (span in strikethroughSpans) {
                spannable.removeSpan(span)
            }
            val colorSpans =
                spannable.getSpans(0, spannable.length, ForegroundColorSpan::class.java)
            for (span in colorSpans) {
                spannable.removeSpan(span)
            }
        }
        editText.text = spannable
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

    fun setCheckBoxChangeListener(listener: ContentCheckBoxChangeListener) {
        checkBoxChangeListener = listener
    }

    fun setContentDeleteListener(listener: ContentDeleteListener) {
        contentDeleteListener = listener
    }
}