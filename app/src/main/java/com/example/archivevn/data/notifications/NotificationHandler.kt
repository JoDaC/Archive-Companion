package com.example.archivevn.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.archivevn.R

private const val CHANNEL_ID = "my_notification_channel_id"
private const val NOTIFICATION_ID = 1

class NotificationHandler(private val context: Context) {

    /**
     * Shows a loading notification to indicate that the page is being archived.
     */
    fun showLoadingNotification() {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
            Log.i("NotificationTag", "notify() called")
        }
    }

    fun showTestNotification() {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(context.getString(R.string.test_notification_title))
            .setContentText(context.getString(R.string.test_notification_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

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