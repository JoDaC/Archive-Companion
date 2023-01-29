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
import androidx.appcompat.app.AlertDialog
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v(tag, "onNewIntent")
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
                            launchUrlInBackground(sharedText, "No results")
                        }
                    }
                }
            }
        }
    }

    private fun launchUrlInBrowser(url: String, urlToArchive: Boolean ?= null) {
        Log.i("Shared URL %" , url)
        var archiveUrl = "https://archive.vn/$url"
        if (urlToArchive == true) {
            archiveUrl = "https://archive.is/?$url"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(archiveUrl))
        startActivity(browserIntent)
    }

    private fun launchUrlInBackground(url: String, searchTerm: String, urlToArchive: Boolean ?= null) {
        Log.i("Shared URL %" , url)
        var archiveUrl = "https://archive.vn/$url"
        if (urlToArchive == true) {
            archiveUrl = "https://archive.is/?url=$url"
        }
        val loader = OkHttpHandler(archiveUrl)
        val result = loader.loadUrl(searchTerm)
        if (result && searchTerm == "No results") {
            archiveDialog(url)
        }
        if (result && searchTerm == "Save") {
            // sending the searchTerm to search for in the parsed body of the page.
            // Return a final URL of the archived page. or, at least it should - this will probably break rn idfk
            val archivedResult = loader.bodyParserAndLinkRequest(searchTerm)
            Log.i("Final URL of Archived page ", archivedResult)
            archiveConfirmedDialog(archivedResult)
        }
    }

    private fun archiveDialog(url: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No Archived Page Found")
        builder.setMessage("Do you want to archive this page?")
        builder.setPositiveButton("Yes") { _, _ ->
            launchUrlInBackground(url, "Save", true)
        }
        builder.setNegativeButton("No") { _, _ ->
        }
        builder.setNeutralButton("Launch in Browser") { _, _ ->
            launchUrlInBrowser(url)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun archiveConfirmedDialog(url: String ?= null) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Page has been archived!")
        builder.setMessage("Do you want to view in your browser?")
        builder.setPositiveButton("Yes") { _, _ ->
            launchUrlInBrowser(url!!)
            // handle 'yes' button click
            // code for archiving the page goes here
            // val urlToArchive = "https://archive.is/?url=$url"
        }
        builder.setNegativeButton("No") { _, _ ->
            // handle 'no' button click
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

