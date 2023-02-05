package com.example.archivevn

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.*

class MainActivity : AppCompatActivity() {
    private lateinit var urlEditText: EditText
    private lateinit var goButton: Button
    private lateinit var readerButton: Button
    private lateinit var loadingWheel: ProgressBar
    private lateinit var fragmentContainerView: FrameLayout
    private val tag = "MainActivityTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize text field and button IDs.
        urlEditText = findViewById(R.id.url_edit_text)
        goButton = findViewById(R.id.go_button)
        readerButton = findViewById(R.id.reader_button)
        loadingWheel = findViewById(R.id.progress_bar)
        fragmentContainerView = findViewById(R.id.fragmentContainerView)
        loadingWheel.visibility = View.GONE

        // Create Notification Channel
        NotificationHandler(this).createNotificationChannel()

        // Set onClickListener for GO button.
        goButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (url.isNotEmpty()) {
                launchUrlInBrowser(url)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }
        // Set onClickListener for Reader button to launch Reader fragment.
        readerButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (url.isNotEmpty()) {
                launchUrlInReader(url)
            } else {
                Toast.makeText(this, "Please enter a URL to view in Reader", Toast.LENGTH_SHORT)
                    .show()
                // delivering push notification here for easy testing purposes
                NotificationHandler(this@MainActivity).showTestNotification()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v(tag, "onNewIntent")
        handleShareSheetUrlInBackground(intent)
    }

//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

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

    private fun launchUrlInReader(url: String) {
        Log.i("Shared URL %", url)
        val readerFragment = ReaderFragment.newInstance(url)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, readerFragment)
            .commit()
        fragmentContainerView.visibility = View.VISIBLE
    }

    private fun launchUrlInBrowser(url: String, urlToArchive: Boolean? = null) {
        Log.i("Shared URL %", url)
        var archiveUrl = "https://archive.vn/$url"
        if (urlToArchive == true) {
            archiveUrl = "https://archive.is/?$url"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun launchUrlInBackground(url: String, urlToArchive: Boolean? = null) {
        Log.i("Shared URL %", url)
        var archiveUrl = "https://archive.vn/$url"
        if (urlToArchive == true) {
            archiveUrl = "https://archive.is/?url=$url"
        }
        val loader = OkHttpHandler(archiveUrl)
        Log.i("URL to be sent to OkHttpHandler: ", archiveUrl)
        MainScope().launch {
            Log.i(tag, "Checking Page to see if URL archived or not %")
            loadingWheel.visibility = View.VISIBLE
            when (loader.loadUrl()) {
                "No results" -> {
                    Log.i(tag, "Displaying Archive Dialog")
                    archiveDialog(url)
                }
                "Newest" -> {
                    Log.i(tag, "Displaying Archive Dialog")
                    linkFoundDialog(url)
                }
                "My url is alive and I want to archive its content" -> {
                    Log.i(tag, "Triggering page archival and displaying Archived Dialog")
                    // THIS IS WHERE I NEED TO TRIGGER THE PUSH NOTIFICATION.
                    var archivedResult = ""
                    while (archivedResult.contains("https://archive.ph/wip/")) {
                        NotificationHandler(this@MainActivity).showLoadingNotification()
                        delay(1000)
                    }
                    archivedResult = loader.launchPageArchival(url)
                    Log.i("Final URL of Archived page ", archivedResult)
                    archiveConfirmedDialog(archivedResult)
                }
            }
            loadingWheel.visibility = View.GONE
        }
    }

    private fun archiveDialog(url: String) {
        Log.i(tag, "First Time archiveDialog() started")
        val builder = AlertDialog.Builder(this).apply { }
            .setTitle("No Archived Page Found")
            .setMessage("Do you want to archive this page?")
            .setPositiveButton("Yes") { _, _ ->
                launchUrlInBackground(url, true)
            }
            .setNegativeButton("No") { _, _ ->
            }
            .setNeutralButton("Launch in Browser") { _, _ ->
                launchUrlInBrowser("https://archive.vn/$url")
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun linkFoundDialog(url: String) {
        Log.i(tag, "linkFoundDialog() started")
        val latestArchiveUrl = "http://archive.is/newest/$url"
        val builder = AlertDialog.Builder(this).apply { }
            .setTitle("Archived Page for this URL has been found")
            .setMessage("Do you want to view in your browser or read now?")
            .setPositiveButton("Launch in Browser") { _, _ ->
                launchUrlInBrowser(latestArchiveUrl)
            }
            .setNeutralButton("Launch in Reader") { _, _ ->
                Log.i("linkToSendFragment", latestArchiveUrl)
                // launch code for text extraction
                launchUrlInReader(latestArchiveUrl)
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun archiveConfirmedDialog(url: String? = null) {
        Log.i(tag, "archiveConfirmedDialog() started")
        val builder = AlertDialog.Builder(this).apply { }
            .setTitle("Page has been archived!")
            .setPositiveButton("View in Browser") { _, _ ->
                launchUrlInBrowser(url!!)
            }
            .setNeutralButton("View in Reader") { _, _ ->
                launchUrlInReader(url!!)
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

