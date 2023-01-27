package com.example.archivevn

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*

class MainActivity : AppCompatActivity() {
    private lateinit var urlEditText: EditText
    private lateinit var goButton: Button
    private val tag = "MainActivityTag"

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize text field and button IDs.
        urlEditText = findViewById<EditText>(R.id.url_edit_text)
        goButton = findViewById(R.id.go_button)

        // Set onClickListener for GO button.
        goButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (url.isNotEmpty()) {
                launchUrlInBrowser(url)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(tag, "onStart")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v(tag, "onNewIntent")
//        handleShareSheetUrlInBrowser(intent)
        handleShareSheetUrlInBackground(intent)
    }

    override fun onResume() {
        super.onResume()
        Log.v(tag, "onResume")
        // Handle URL sent to the app via the Android share sheet
//        handleShareSheetUrlInBrowser(intent)
        handleShareSheetUrlInBackground(intent)
    }

    private fun handleShareSheetUrlInBrowser(intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                        if (sharedText != null) {
                            launchUrlInBrowser(sharedText)
                        }
                    }
                }
            }
        }
    }

    private fun handleShareSheetUrlInBackground(intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                        if (sharedText != null) {
                            launchUrlInBackground(sharedText)
                        }
                    }
                }
            }
        }
    }

    private fun launchUrlInBrowser(url: String) {
        Log.i("Shared URL %" , url)
        val archiveUrl = "https://archive.vn/$url"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(archiveUrl))
        startActivity(browserIntent)
    }

    private fun launchUrlInBackground(url: String) {
        Log.i("Shared URL %" , url)
        val archiveUrl = "https://archive.vn/$url"
        val loader = OkHttpHandler(this, archiveUrl)
        loader.load()
    }
}

