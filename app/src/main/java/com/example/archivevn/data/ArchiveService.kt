package com.example.archivevn.data

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.data.notifications.NotificationHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.net.URLEncoder

private const val NOTIFICATION_ID = 1

class ArchiveService : Service() {

    private var client = OkHttpClient()

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

    suspend fun archiveUrlInBackground(url: String): Pair<String, String?> {
        val loader = OkHttpHandler(url)
        val archivedResult = launchPageArchival(url)
        Log.i("Final URL of Archived page ", archivedResult)
        val articleTitle = loader.fetchExtractedTitleAndText(archivedResult).second
        stopSelf()
        return Pair(archivedResult, articleTitle)
    }

    /**
     * Launches the page archival process using the specified URL and the archive.today service.
     *
     * @param url The URL to archive.
     * @return The URL of the archived page.
     */
    private suspend fun launchPageArchival(url: String): String {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .build()
            val responseOne = client.newCall(request).execute()
            val responseBody = responseOne.body()?.string()
            val parsedBody = Jsoup.parse(responseBody!!)
            val submitId = parsedBody.select("[name='submitId']").first()?.attr("value")
            if (submitId != null) {
                Log.i("submitId is ", submitId)
            } else {
                Log.e("launchPageArchival", "submitId is null")
            }
            val encodedUrl = URLEncoder.encode(url, "UTF-8")
            val fullRequestString = "https://archive.ph/submit/?submitid=$submitId&url=$encodedUrl"
            Log.i("fullRequestString is ", fullRequestString)
            val requestTwo = Request.Builder()
                .url(fullRequestString)
                .build()
            var responseTwo = client.newCall(requestTwo).execute()
            while (responseTwo.toString().contains("https://archive.ph/submit/?submitid=")) {
                responseTwo = client.newCall(requestTwo).execute()
                Log.i("big_ass_waffles", responseTwo.toString())
                // Currently using a very large polling time to avoid captcha.
                delay(30000)
            }
            val urlToTriggerArchival = responseTwo.request().url().toString()
            Log.i("URL to trigger Archival ", urlToTriggerArchival)
            responseOne.body()?.close()
            responseTwo.body()?.close()
            urlToTriggerArchival
        }
    }
}

