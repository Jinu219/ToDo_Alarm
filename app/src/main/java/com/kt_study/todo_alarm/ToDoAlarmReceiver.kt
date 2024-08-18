package com.kt_study.todo_alarm

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ToDoAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.e("ToDoAlarmReceiver", "Context or Intent is null")
            return
        }
        Log.d("alarm log in Receiver", "Broadcast received!!")
        if (intent.extras?.getInt("code") == MainActivity.REQUEST_CODE) {
            // 알림 채널 생성
            createNotificationChannel(context)
            Log.d("alarm log in Receiver", "alarm received!!")

            // 알림을 눌렀을 때 앱이 켜지도록 intent 생성
            val intentForMainActivity = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                MainActivity.REQUEST_CODE,
                intentForMainActivity,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = intent.extras!!.getString("title", "ToDo_Alarm_Notification")
            val contents = "할 일을 해야할 시간입니다"

            // Notification
            var notification = NotificationCompat.Builder(context, "todo_channel").apply {
                setSmallIcon(R.drawable.ic_launcher_background)
                setContentTitle(title)  // Set Title
                setContentText(contents)   // Set Content
                priority = NotificationCompat.PRIORITY_DEFAULT  // Set PRIORITY
                setContentIntent(pendingIntent) // Notification Click Event
                setAutoCancel(true) // Remove After Click Notification
            }

            with(NotificationManagerCompat.from(context)) {
                notify(6, notification.build())
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "todo_channel",
                "ToDo Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for ToDo notifications"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}