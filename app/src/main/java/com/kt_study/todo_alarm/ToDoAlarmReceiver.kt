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

        if (intent!!.extras?.getInt("code") == MainActivity.REQUEST_CODE) {
            var count = intent.getIntExtra("count", 0)
            Log.d("myLog", "$count")

            createNotificationChannel(context)

            val intentForMainActivity = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                MainActivity.REQUEST_CODE,
                intentForMainActivity,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val contents = "Contents Contents Contents Contents Contents Contents Contents " +
                    "Contents Contents Contents Contents Contents Contents Contents "

            // Notification
            var notification = NotificationCompat.Builder(context, "todo_channel").apply {
                setSmallIcon(R.drawable.ic_launcher_background)
                setContentTitle("Title 1")  // Set Title
                setContentText(contents)   // Set Content
                priority = NotificationCompat.PRIORITY_DEFAULT  // Set PRIORITY
                setContentIntent(pendingIntent) // Notification Click Event
                setAutoCancel(true) // Remove After Click Notification
            }

            with(NotificationManagerCompat.from(context)) {
                notify(5, notification.build())
            }
        }


//        context?.let {
//            val notificationManager = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            val notification = NotificationCompat.Builder(it, "todo_channel")
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle("Reminder")
//                .setContentText("It's time to do something")
//                .setAutoCancel(true)
//                .build()
//
//            notificationManager.notify(0, notification)
//        }
    }

    fun createNotificationChannel(context: Context) {
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