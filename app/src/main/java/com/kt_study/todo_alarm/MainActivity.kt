package com.kt_study.todo_alarm

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kt_study.todo_alarm.categories.CategoryAdapter
import com.kt_study.todo_alarm.categories.CategoryAlarmBtnClickListener
import com.kt_study.todo_alarm.categories.CategoryCheckBoxChangeListener
import com.kt_study.todo_alarm.categories.CategoryContentDeleteListener
import com.kt_study.todo_alarm.categories.CategoryItem
import com.kt_study.todo_alarm.categories.CategoryTextChangeListener
import com.kt_study.todo_alarm.categories.contents.ContentItem
import com.kt_study.todo_alarm.databinding.ActivityMainBinding
import com.kt_study.todo_alarm.db.CategoryEntity
import com.kt_study.todo_alarm.db.ContentEntity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private val alarmManager by lazy { binding.root.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private var isAlarmSetting = false
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            application as ToDoApplication,
            (application as ToDoApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserve()
        initBtn()
    }


    private fun initObserve() {
        viewModel.categories.observe(this) { categories ->
            categoryAdapter = CategoryAdapter(
                this,
                categories.toMutableList(),
                { position ->
                    val categoryId = categories[position].id
                    viewModel.makeContent(position, categoryId, "", 0, 0, false)
                },
                { isChecked, content ->
                    val pendingIntent =
                        Intent(binding.root.context, ToDoAlarmReceiver::class.java).let {
                            it.putExtra("code", REQUEST_CODE)
                            it.putExtra("title", content.toDo)
                            PendingIntent.getBroadcast(
                                binding.root.context,
                                REQUEST_CODE,
                                it,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        }

                    if (isChecked) {
                        // 알람 권한 확인 - API가 33 이상일때만 권한 요청이 필요하므로 사용자에게 권한 설정을 요구함
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            checkNotificationPermission()
                        }

                        val triggerTime = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, content.hour)
                                set(Calendar.MINUTE, content.min)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.timeInMillis

                        // 알람 권한 요청을 받은 후
                        // 알람을 설정함
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                        )
                        Log.d("alarm log", "alarm activating")
                    } else {
                        // 알람을 해제함
                        alarmManager.cancel(pendingIntent)
                    }
                }
            )

            categoryAdapter.setCategoryContentDeleteListener(object :
                CategoryContentDeleteListener {
                override fun onContentDelete(categoryPosition: Int, contentItem: ContentItem) {
                    val contentEntity = convertToContentEntity(contentItem)
                    viewModel.deleteContent(contentEntity)
                }
            })

            categoryAdapter.setTextChangeListener(object : CategoryTextChangeListener {
                override fun onContentTextChange(
                    categoryPosition: Int,
                    contentPosition: Int,
                    toDo: String,
                ) {
                    val categoryId = categories[categoryPosition].id
                    val contentId = categories[categoryPosition].contents[contentPosition].contentId
                    val contentItem = categoryAdapter.getContentItem(
                        categoryPosition = categoryPosition,
                        contentPosition = contentPosition
                    )
                    val contentEntity = convertToContentEntity(contentItem)
                    val updatedContentEntity = contentEntity.copy(
                        contentId = contentId,
                        categoryId = categoryId,
                        toDo = toDo,
                        hour = contentEntity.hour,
                        min = contentEntity.min,
                        isChecked = contentEntity.isChecked

                    )
                    viewModel.updateContent(updatedContentEntity)
                }

                override fun onTextChange(categoryPosition: Int, title: String) {
                    val categoryId = categories[categoryPosition].id
                    val categoryItem = categories[categoryPosition]
                    val categoryEntity = convertToCategoryEntity(categoryItem)
                    val updatedCategoryEntity = categoryEntity.copy(
                        id = categoryId,
                        title = title
                    )
                    viewModel.updateCategory(updatedCategoryEntity)
                }
            })

            categoryAdapter.setCheckBoxChangeListener(object : CategoryCheckBoxChangeListener {
                override fun onCheckBoxChanged(
                    categoryPosition: Int,
                    contentPosition: Int,
                    isChecked: Boolean,
                ) {
                    val contentItem =
                        categoryAdapter.getContentItem(categoryPosition, contentPosition)
                    val contentEntity = convertToContentEntity(contentItem)
                    val categoryId = categories[categoryPosition].id
                    val contentId = contentItem.contentId
                    val updatedEntity = contentEntity.copy(
                        categoryId = categoryId,
                        contentId = contentId,
                        isChecked = isChecked
                    )
                    viewModel.updateContent(updatedEntity)
                }
            })

            categoryAdapter.setContentClickListener(object : CategoryAlarmBtnClickListener {
                override fun onContentClick(
                    parentPosition: Int,
                    childPosition: Int,
                    currentHour: Int,
                    currentMin: Int,
                    updateTimeCallBack: (hour: Int, min: Int) -> Unit,
                ) {
                    isAlarmSetting = true
                    val contentItem = categoryAdapter.getContentItem(parentPosition, childPosition)
                    val contentEntity = convertToContentEntity(contentItem)
                    val alarmFragment = AlarmFragment(
                        initialHour = contentItem.hour,
                        initialMin = contentItem.min
                    ) { selectedHour, selectedMin ->
                        val contentId = categories[parentPosition].contents[childPosition].contentId
                        val updatedContentEntity =
                            contentEntity.copy(
                                contentId = contentId,
                                toDo = contentItem.toDo,
                                hour = selectedHour,
                                min = selectedMin,
                                isChecked = contentItem.isChecked
                            )
                        viewModel.updateContent(updatedContentEntity)
                        updateTimeCallBack(selectedHour, selectedMin)
                        isAlarmSetting = false
                    }
                    alarmFragment.show(supportFragmentManager, "alarmFragment")
                }
            })
            binding.rvCategory.adapter = categoryAdapter
        }

    }

    private fun initBtn() {
        binding.btnAddCategory.setOnClickListener {
            viewModel.makeCategory("")
        }
    }

    fun convertToCategoryEntity(categoryItem: CategoryItem): CategoryEntity = CategoryEntity(
        id = categoryItem.id,
        title = categoryItem.title
    )

    fun convertToContentEntity(contentItem: ContentItem): ContentEntity = ContentEntity(
        contentId = contentItem.contentId,
        categoryId = contentItem.categoryId,
        toDo = contentItem.toDo,
        hour = contentItem.hour,
        min = contentItem.min,
        isChecked = contentItem.isChecked
    )

    fun convertToContentItem(contentEntity: ContentEntity): ContentItem = ContentItem(
        contentId = contentEntity.contentId,
        categoryId = contentEntity.categoryId,
        toDo = contentEntity.toDo,
        hour = contentEntity.hour,
        min = contentEntity.min,
        isChecked = contentEntity.isChecked
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED // 알림 권한이 없을 때
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용됨
                Log.d("Alarm Log", "Notification Permission Success")
            } else {
                // 권한이 거부됨
                // Switch 버튼을 off로 바꿈
//                val pendingIntent =
//                    Intent(binding.root.context, ToDoAlarmReceiver::class.java).let {
//                        it.putExtra("code", REQUEST_CODE)
//                        it.putExtra("count", 10)
//                        PendingIntent.getBroadcast(
//                            binding.root.context,
//                            REQUEST_CODE,
//                            it,
//                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                        )
//                    }
//                alarmManager.cancel(pendingIntent)
                Toast.makeText(this, "알림 권한을 활성화 해주세요", Toast.LENGTH_SHORT).show()
                Log.d("Alarm Log", "Notification Permission Failed")
            }
        }
    }


    companion object {
        const val REQUEST_CODE = 101
    }
}