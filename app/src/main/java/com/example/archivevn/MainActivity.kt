package com.example.archivevn

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    private lateinit var urlEditText: EditText
    private lateinit var goButton: Button

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlEditText = findViewById(R.id.url_edit_text)
        goButton = findViewById(R.id.go_button)

        val intent = intent
        val type = intent.type

        // Handle URL sent to the app via the Android share sheet
        // handleShareSheetUrl()
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == type) {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    println(sharedText)
                    if (sharedText != null) {
                        launchUrlInBrowser(sharedText)
                    }
                }
            }
        }

        goButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (url.isNotEmpty()) {
                launchUrlInBrowser(url)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchUrlInBrowser(url: String) {
        val archiveUrl = "https://archive.vn/$url"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(archiveUrl))
        startActivity(browserIntent)
    }

//    private fun handleShareSheetUrl() {
//        val intent = intent
//        val action = intent.action
//        val type = intent.type
//
//        if (Intent.ACTION_SEND == action && type != null) {
//            if ("text/plain" == type) {
//                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
//                if (sharedText != null) {
//                    launchUrlInBrowser(sharedText)
//                }
//            }
//        }
//    }
}

