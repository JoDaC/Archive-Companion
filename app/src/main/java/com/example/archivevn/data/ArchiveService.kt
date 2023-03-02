package com.example.archivevn.data

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.data.notifications.NotificationHandler

private const val NOTIFICATION_ID = 1

class ArchiveService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val notification = NotificationHandler(application).loadingNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the service
        stopSelf()
    }

    suspend fun archiveUrlInBackground(
        url: String,
        loader: OkHttpHandler,
    ): Pair<String, String?> {
        val archivedResult = loader.launchPageArchival(url)
        Log.i("Final URL of Archived page ", archivedResult)
        val articleTitle = loader.fetchExtractedTitleAndText(archivedResult).second
        return Pair(archivedResult, articleTitle)
    }
}
