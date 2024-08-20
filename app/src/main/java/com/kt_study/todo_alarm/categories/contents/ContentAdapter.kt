package com.kt_study.todo_alarm.categories.contents

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kt_study.todo_alarm.R
import com.kt_study.todo_alarm.databinding.ItemContentBinding

class ContentAdapter(
    private val context: Context,
    private val contents: MutableList<ContentItem>,
) : RecyclerView.Adapter<ContentViewHolder>() {

    private lateinit var alarmClickListener: ContentAlarmBtnClickListener
    private lateinit var textChangeListener: ContentTextChangeListener
    private lateinit var checkBoxChangeListener: ContentCheckBoxChangeListener
    private lateinit var contentDeleteListener: ContentDeleteListener
    private lateinit var contentNotificationListener: ContentNotificationListener

    // 아이콘과 배경색 설정
    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)
    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_edit)
    private val iconSize = 50 // dp를 px로 변환해야 합니다
    private val deleteBackground = ColorDrawable(Color.parseColor("#ff4545"))
    private val editBackground = ColorDrawable(Color.BLUE)
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = context.resources.getDimensionPixelSize(R.dimen.swipe_text_size).toFloat()
        textAlign = Paint.Align.RIGHT
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private var isSwiping = false

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
        if (position < 0 || position >= contents.size) {
            Log.e("ContentAdapter", "Invalid position: $position")
            return
        }

        holder.bind(contents[position])

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
            if (!isSwiping) {
                checkBoxChangeListener.onCheckBoxChanged(
                    ContentItem(
                        contentId = position,
                        categoryId = item.categoryId,
                        toDo = item.toDo,
                        hour = item.hour,
                        min = item.min,
                        isChecked = item.isChecked,
                        isNotificationEnabled = item.isNotificationEnabled
                    )
                )
                updateEditTextStyle(
                    holder.binding.etContent,
                    item.isChecked,
                    holder.binding.etContent.text.toString()
                )
            }
        }

        holder.textWatcher?.let { holder.binding.etContent.removeTextChangedListener(it) }
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isSwiping) {
                    s?.let {
                        // Adapter의 position과 텍스트를 전달하여 변경 사항을 알립니다.
                        textChangeListener.onTextChange(holder.adapterPosition, it.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        holder.binding.etContent.addTextChangedListener(textWatcher)
        holder.textWatcher = textWatcher

        // 알림을 켜고 끌 수 있는 Switch
        holder.binding.swNotification.setOnCheckedChangeListener { _, isNotificationEnabled ->
            contentNotificationListener.onActiveAlarm(holder.adapterPosition, isNotificationEnabled)
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
                if (position != RecyclerView.NO_POSITION && position < contents.size) {
                    val item = contents[position]  // 현재 스와이프된 아이템
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            isSwiping = true
                            deleteItem(position)
                        }

                        ItemTouchHelper.RIGHT -> {
                            isSwiping = true
                            val contentViewHolder = viewHolder as ContentViewHolder
                            triggerAlarm(contentViewHolder, position, item)
                            notifyItemChanged(position)
                        }
                    }
                }
                isSwiping = false
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - iconSize) / 2

                if (dX > 0) { // 스와이프 오른쪽
                    editIcon?.setBounds(
                        itemView.left + iconMargin,
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + iconSize,
                        itemView.bottom - iconMargin
                    )
                    editBackground?.setBounds(
                        itemView.left, itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )
                    editBackground?.draw(c)
                    editIcon?.draw(c)

                    val textToShow = "알람"
                    val textX = itemView.left + dX - 120
                    val textY = itemView.top + (itemView.height / 2) + (textPaint.textSize / 2)
                    c.drawText(textToShow, textX, textY, textPaint)

                } else if (dX < 0) { // 스와이프 왼쪽
                    deleteIcon?.setBounds(
                        itemView.right - iconMargin - iconSize,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                    deleteBackground?.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteBackground?.draw(c)
                    deleteIcon?.draw(c)

                    // "삭제" 텍스트 그리기
                    val textToShow = "삭제"
                    val textX = itemView.right + dX + 120
                    val textY = itemView.top + (itemView.height / 2) + (textPaint.textSize / 2)
                    c.drawText(textToShow, textX, textY, textPaint)
                }

                isSwiping = isCurrentlyActive

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

    fun deleteItem(position: Int) {
        if (position < 0 || position >= contents.size) {
            Log.e("ContentAdapter", "Invalid position: $position")
            return
        }

        val deletedItem = contents[position]
        contents.removeAt(position) // 데이터 리스트에서 아이템 삭제
        notifyItemRemoved(position) // RecyclerView에 삭제된 아이템 알리기

        // 삭제된 아이템의 정보를 삭제 리스너에 전달
        contentDeleteListener.onContentDelete(deletedItem)
    }

    private fun triggerAlarm(holder: ContentViewHolder, position: Int, item: ContentItem) {
        val currentText = holder.binding.etContent.text.toString()
        item.toDo = currentText

        alarmClickListener.onAlarmBtnClick(position) { hour, min ->
            item.hour = hour
            item.min = min
            holder.binding.tvAlarmTime.text =
                context.getString(R.string.to_do_time, item.hour, item.min)
            notifyItemChanged(position)
        }
    }

    private fun updateEditTextStyle(editText: EditText, isChecked: Boolean, text: String) {
        val spannable = SpannableStringBuilder(text)
        if (isChecked) {
            val length = spannable.length
            spannable.setSpan(
                StrikethroughSpan(),
                0,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(Color.GRAY),
                0,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            val strikethroughSpans =
                spannable.getSpans(0, spannable.length, StrikethroughSpan::class.java)
            val colorSpans =
                spannable.getSpans(0, spannable.length, ForegroundColorSpan::class.java)
            for (span in strikethroughSpans) {
                spannable.removeSpan(span)
            }
            for (span in colorSpans) {
                spannable.removeSpan(span)
            }
        }
        editText.text = spannable
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

    fun setContentNotificationListener(listener: ContentNotificationListener) {
        contentNotificationListener = listener
    }
}