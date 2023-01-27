package com.example.archivevn

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi

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
//        urlEditText = findViewById<EditText>(R.id.url_edit_text)
        // Initialize animator object and trigger animation.
//        val animator = ObjectAnimator.ofFloat(urlEditText, "translationY", 0.25f, -urlEditText.height.toFloat())
//        animator.duration = 500
//        animator.start()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v(tag, "onNewIntent")
        handleShareSheetUrl(intent)
    }

    override fun onResume() {
        super.onResume()
        Log.v(tag, "onResume")
        // Handle URL sent to the app via the Android share sheet
        handleShareSheetUrl(intent)
    }

    override fun onPause() {
        super.onPause()
        Log.v(tag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v(tag, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(tag, "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.v(tag, "onRestart")
    }

    private fun handleShareSheetUrl(intent: Intent?) {
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

    private fun launchUrlInBrowser(url: String) {
        Log.i("Shared URL %" , url)
        val archiveUrl = "https://archive.vn/$url"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(archiveUrl))
        startActivity(browserIntent)
    }
}

