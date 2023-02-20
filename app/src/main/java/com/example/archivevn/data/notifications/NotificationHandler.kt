package com.example.archivevn.data.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.archivevn.R
import com.example.archivevn.view.MainActivity


private const val CHANNEL_ID = "my_notification_channel_id"
private const val NOTIFICATION_ID = 1

class NotificationHandler(private val context: Context) {
    private val intent = Intent(context, MainActivity::class.java)

    /**
     * Shows a loading notification to indicate that the page is being archived.
     */
    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    fun showLoadingNotification() {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(intent)
            .setOngoing(true)
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
            Log.i("NotificationTag", "notify() called")
        }
    }

    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    fun showArchivalCompleteNotification() {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Page Archival is Complete")
            .setContentText("Your page is ready to be viewed.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
            Log.i("NotificationTag", "notify() called")
        }
    }

    @SuppressLint("MissingPermission")
    fun showTestNotification() {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(context.getString(R.string.test_notification_title))
            .setContentText(context.getString(R.string.test_notification_message))
            .setContentIntent(intent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
            Log.i("NotificationTag", "notify() called")
        }
    }

    class NotificationChannel(private val context: Context) {
        private val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.channel_name)
                val descriptionText = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
                Log.d("NotificationTag", "Notification channel created")
            }
        }

        fun closeNotification() {
            notificationManager.cancelAll()
        }
    }
}