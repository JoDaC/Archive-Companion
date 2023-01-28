package com.example.archivevn

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AlertDialog
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpHandler(private val url: String) {

    fun loadUrl(searchTerm: String): Boolean {
        var result = false
        Thread {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
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
        }.start()
        //TODO()Remove sleep and use either coroutines or a callback
        Thread.sleep(1000)
        return result
    }
}