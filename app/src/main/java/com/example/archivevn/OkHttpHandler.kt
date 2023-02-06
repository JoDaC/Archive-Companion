package com.example.archivevn

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.net.URLEncoder

class OkHttpHandler(url: String) {

    private var client = OkHttpClient()
    private val request = Request.Builder()
        .url(url)
        .build()

    suspend fun loadUrlAndParseToString(): String {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            val parsedBody = Jsoup.parse(responseBody!!)
            Log.d("Parsed Body", parsedBody.toString())
//            val articleBody = parsedBody.select("[name='articleBody']")
            Log.d("Article Body", parsedBody.toString())
            parsedBody.toString()
        }
    }

    suspend fun loadUrl(): String {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            val searchTerms =
                listOf("No results", "Newest", "My url is alive and I want to archive its content")
            var resultString = ""
            for (searchTerm in searchTerms) {
                if (responseBody != null && responseBody.contains(searchTerm)) {
                    resultString = searchTerm
                    break
                }
            }
            if (resultString.isEmpty()) {
                Log.d("No search terms found in response body", responseBody!!)
            }
            resultString
        }
    }

    suspend fun launchPageArchival(url: String): String {
        return withContext(Dispatchers.IO) {
            val responseOne = client.newCall(request).execute()
            val responseBody = responseOne.body()?.string()
            val parsedBody = Jsoup.parse(responseBody!!)
            val submitId = parsedBody.select("[name='submitId']").first()?.attr("value")
            Log.i("submitId is ", submitId!!)
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
                delay(10000)
            }
            val urlToTriggerArchival = responseTwo.request().url().toString()
            Log.i("URL to trigger Archival ", urlToTriggerArchival)
            urlToTriggerArchival
        }
    }
}