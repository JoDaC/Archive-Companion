package com.example.archivevn

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import kotlinx.coroutines.*

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

class OkHttpHandler(private val url: String, val parseAndArchive: Boolean ?= null) {

    private var result = false
    private val client = OkHttpClient()
    private val request = Request.Builder()
        .url(url)
        .build()
    private val response = client.newCall(request).execute()
    private val responseBody = response.body()?.string()

    fun loadUrl(searchTerm: String): Boolean {
        GlobalScope.launch {
            result = responseBody.let { !it.isNullOrEmpty() && it.contains(searchTerm) }
            if (result) {
                if (responseBody != null) {
                    Log.d("Response Body", responseBody)
                }
            } else {
                Log.d("$searchTerm not found in Response Body", responseBody!!)
            }
        }
        return result
    }

    fun bodyParserAndLinkRequest(searchTerm: String): String {
        GlobalScope.launch {
            val parsedBody = Jsoup.parse(responseBody)
            val elements = parsedBody.select("a:contains($searchTerm)")
            for (element in elements) {
                val href = element.attr("href")
                val request = Request.Builder().url(href).build()
                val response = client.newCall(request).execute()
            }
        }
        return response.request().url().toString()
    }
}