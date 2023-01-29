package com.example.archivevn

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class OkHttpHandler(url: String) {

    private var result = false
    private val client = OkHttpClient()
    private val request = Request.Builder()
        .url(url)
        .build()

    suspend fun loadUrl(searchTerm: String): Boolean {
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

    suspend fun bodyParserAndLinkRequest(searchTerm: String): String {
        return withContext(Dispatchers.Default) {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            val parsedBody = Jsoup.parse(responseBody!!)
            val elements = parsedBody.select("a:contains($searchTerm)")
            var url = ""
            for (element in elements) {
                val href = element.attr("href")
                Log.i("href is ",href)
                val request = Request.Builder().url(href).build()
                val response = client.newCall(request).execute()
                url = response.request().url().toString()
            }
            Log.i("Final URL is ",url)
            url
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