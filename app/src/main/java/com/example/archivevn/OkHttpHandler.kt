package com.example.archivevn

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.net.URLEncoder

class OkHttpHandler(url: String) {

    private var result = false
    private val client = OkHttpClient()
    private val request = Request.Builder()
        .url(url)
        .build()

    suspend fun loadUrlWithSearchTerm(searchTerm: String): Boolean {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            result = responseBody.let { !it.isNullOrEmpty() && it.contains(searchTerm) }
            if (result) {
                if (responseBody != null) {
                    Log.d("Response Body", responseBody)
                }
            } else {
                Log.d("$searchTerm not found in Response Body", responseBody!!)
            }
            result
        }
    }

    suspend fun loadUrl(): String {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            val searchTerms = listOf("No results", "Newest", "My url is alive and I want to archive its content")
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
        return withContext(Dispatchers.Default) {
            val responseOne = client.newCall(request).execute()
            val responseBody = responseOne.body()?.string()
            val parsedBody = Jsoup.parse(responseBody!!)
            val submitId = parsedBody.select("[name='submitId']").first().toString()
            Log.i("submitId is ",submitId)
            val encodedUrl = URLEncoder.encode(url, "UTF-8")
            val fullRequestString = "/submit/?submitid=$submitId=&url$encodedUrl"
            Log.i("fullRequestString is ",fullRequestString)
            val request = Request.Builder().url(fullRequestString).build()
            val responseTwo = client.newCall(request).execute()
            val fullRequest = responseTwo.request().url().toString()
            Log.i("Final URL is ",fullRequest)
            fullRequest
        }
    }
}


//class OkHttpHandler(private val url: String, val parseAndArchive: Boolean ?= null) {
//
//    private var result = false
//    private val client = OkHttpClient()
//    private val request = Request.Builder()
//        .url(url)
//        .build()
//    private val response = client.newCall(request).execute()
//    private val responseBody = response.body()?.string()
//
//    fun loadUrl(searchTerm: String): Boolean {
//        Thread {
//            result = responseBody.let { !it.isNullOrEmpty() && it.contains(searchTerm) }
//            if (result) {
//                if (responseBody != null) {
//                    Log.d("Response Body", responseBody)
//                }
//            } else {
//                Log.d("$searchTerm not found in Response Body", responseBody!!)
//            }
//        }.start()
//        //TODO()Remove sleep and use either coroutines or a callback
//        Thread.sleep(1000)
//        return result
//    }
//
//    fun bodyParserAndLinkRequest(searchTerm: String): String {
//        Thread {
//            val parsedBody = Jsoup.parse(responseBody)
//            val elements = parsedBody.select("a:contains($searchTerm)")
//            for (element in elements) {
//                val href = element.attr("href")
//                val request = Request.Builder().url(href).build()
//                val response = client.newCall(request).execute()
//            }
//        }.start()
//        return response.request().url().toString()
//    }
//}