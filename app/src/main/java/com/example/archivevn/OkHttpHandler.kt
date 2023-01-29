package com.example.archivevn

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class OkHttpHandler(private val url: String, val parseAndArchive: Boolean ?= null) {

    private var result = false
    private val client = OkHttpClient()
    private val request = Request.Builder()
        .url(url)
        .build()
    private val response = client.newCall(request).execute()
    private val responseBody = response.body()?.string()

    fun loadUrl(searchTerm: String): Boolean {
        Thread {
//            val client = OkHttpClient()
//            val request = Request.Builder()
//                .url(url)
//                .build()
//            val response = client.newCall(request).execute()
//            val responseBody = response.body()?.string()

//            if (responseBody != null && parseAndArchive == true) {
//                bodyParserAndLinkRequest(responseBody, searchTerm)
//            }

            result = responseBody.let { !it.isNullOrEmpty() && it.contains(searchTerm) }
            if (result) {
                if (responseBody != null) {
                    Log.d("Response Body", responseBody)
                }
            } else {
                Log.d("$searchTerm not found in Response Body", responseBody!!)
            }
        }.start()
        //TODO()Remove sleep and use either coroutines or a callback
        Thread.sleep(1000)
        return result
    }

    fun bodyParserAndLinkRequest(searchTerm: String): String {
        Thread {
            val parsedBody = Jsoup.parse(responseBody)
            val elements = parsedBody.select("a:contains($searchTerm)")
            for (element in elements) {
                val href = element.attr("href")
                // perform a new request to the link specified in the href attribute
                // or do something else with the href value
                val request = Request.Builder().url(href).build()
                val response = client.newCall(request).execute()
            }
        }.start()
        return response.request().url().toString()
        // use callback function (or coroutine to get the url of the link that was clicked on, outside the thread
        //It seems like the intent is to return the URL of the link that was clicked on, but the variable response is not accessible outside of the thread, so it cannot be returned. One way to solve this would be to pass a callback function as a parameter to the bodyParserAndLinkRequest function, which will be called with the URL of the clicked link as an argument, once the request to the clicked link is finished.
    }
}