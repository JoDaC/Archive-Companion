package com.example.archivevn

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AlertDialog
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpHandler(private val activity: Activity, private val url: String) {

    fun load() {
        Thread {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()

            if (!responseBody.isNullOrEmpty() && !responseBody.contains("No results")) {
                Log.d("Response Body", responseBody)
                activity.runOnUiThread {
                    showArchiveDialog()
                }
            }
        }.start()
    }

    private fun showArchiveDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Archive page")
        builder.setMessage("Do you want to archive this page?")
        builder.setPositiveButton("Yes") { _, _ ->
            // handle 'yes' button click
            // code for archiving the page goes here
            // val urlToArchive = "https://archive.is/?$url"
        }
        builder.setNegativeButton("No") { _, _ ->
            // handle 'no' button click
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}