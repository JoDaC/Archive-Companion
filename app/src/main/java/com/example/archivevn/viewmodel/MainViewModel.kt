package com.example.archivevn.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.archivevn.data.notifications.NotificationHandler
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.R
import com.example.archivevn.databinding.ActivityMainBinding
import com.example.archivevn.view.ReaderFragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var notificationChannel: NotificationHandler.NotificationChannel = NotificationHandler.NotificationChannel(getApplication())
    private lateinit var fragmentManager: FragmentManager
    private val tag = "MainActivityTag"

    init {
        notificationChannel.createNotificationChannel()
    }

    fun onGoButtonClicked(binding: ActivityMainBinding) {
        val url = binding.urlEditText.text.toString()
        if (url.isNotEmpty()) {
            launchUrlInBrowser(url, getApplication())
        } else {
            Toast.makeText(getApplication(), "Please enter a URL", Toast.LENGTH_SHORT).show()
        }
    }

    fun onReaderButtonClicked(binding: ActivityMainBinding) {
        val url = binding.urlEditText.text.toString()
        if (url.isNotEmpty()) {
            launchUrlInReader(url, binding)
        } else {
            Toast.makeText(getApplication(), "Please enter a URL to view in Reader", Toast.LENGTH_SHORT)
                .show()
            // delivering push notification here for easy testing purposes
            NotificationHandler(getApplication()).showTestNotification()
            SystemClock.sleep(5000)
            notificationChannel.closeNotification()
        }
    }


    private fun launchUrlInBrowser(url: String, context: Context, urlToArchive: Boolean? = null) {
        Log.i("Shared URL %", url)
        var archiveUrl = "https://archive.vn/$url"
        if (urlToArchive == true) {
            archiveUrl = "https://archive.is/?$url"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    private fun launchUrlInReader(url: String, binding: ActivityMainBinding) {
        Log.i("Shared URL %", url)
        val readerFragment = ReaderFragment.newInstance(url)
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, readerFragment)
            .commit()
        binding.fragmentContainerView.visibility = View.VISIBLE
    }

    private fun launchUrlInBackground(url: String, binding: ActivityMainBinding, urlToArchive: Boolean? = null) {
        Log.i("Shared URL %", url)
        var archiveUrl = "https://archive.vn/$url"
        if (urlToArchive == true) {
            archiveUrl = "https://archive.is/?url=$url"
        }
        val loader = OkHttpHandler(archiveUrl)
        Log.i("URL to be sent to OkHttpHandler: ", archiveUrl)
        MainScope().launch {
            Log.i(tag, "Checking Page to see if URL archived or not %")
            binding.progressBar.visibility = View.VISIBLE
            when (loader.loadUrl()) {
                "No results" -> {
                    Log.i(tag, "Displaying Archive Dialog")
                    archiveDialog(url, binding)
                }
                "Newest" -> {
                    Log.i(tag, "Displaying Archive Dialog")
                    linkFoundDialog(url, binding)
                }
                "My url is alive and I want to archive its content" -> {
                    Log.i(tag, "Triggering page archival and displaying Archived Dialog")
                    NotificationHandler(getApplication()).showLoadingNotification()
                    val archivedResult = loader.launchPageArchival(url)
                    Log.i("Final URL of Archived page ", archivedResult)
                    archiveConfirmedDialog(archivedResult, binding)
                    val notificationChannel =
                        NotificationHandler.NotificationChannel(getApplication())
                    notificationChannel.closeNotification()
                }
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    fun handleShareSheetUrlInBackground(intent: Intent?, binding: ActivityMainBinding) {
        if (intent != null) {
            when (intent.action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                        if (sharedText != null) {
                            launchUrlInBackground(sharedText, binding)
                        }
                    }
                }
            }
        }
    }

    private fun archiveDialog(url: String, binding: ActivityMainBinding) {
        Log.i(tag, "First Time archiveDialog() started")
        val builder = AlertDialog.Builder(getApplication()).apply { }
            .setTitle("No Archived Page Found")
            .setMessage("Do you want to archive this page?")
            .setPositiveButton("Yes") { _, _ ->
                launchUrlInBackground(url, binding, true)
            }
            .setNegativeButton("No") { _, _ ->
            }
            .setNeutralButton("Launch in Browser") { _, _ ->
                launchUrlInBrowser("https://archive.vn/$url", getApplication())
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun linkFoundDialog(url: String, binding: ActivityMainBinding) {
        Log.i(tag, "linkFoundDialog() started")
        val latestArchiveUrl = "http://archive.is/newest/$url"
        val builder = AlertDialog.Builder(getApplication()).apply { }
            .setTitle("Archived Page for this URL has been found")
            .setMessage("Do you want to view in your browser or read now?")
            .setPositiveButton("Launch in Browser") { _, _ ->
                launchUrlInBrowser(latestArchiveUrl, getApplication())
            }
            .setNeutralButton("Launch in Reader") { _, _ ->
                Log.i("linkToSendFragment", latestArchiveUrl)
                // launch code for text extraction
                launchUrlInReader(latestArchiveUrl, binding)
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun archiveConfirmedDialog(url: String? = null, binding: ActivityMainBinding) {
        Log.i(tag, "archiveConfirmedDialog() started")
        val builder = AlertDialog.Builder(getApplication()).apply { }
            .setTitle("Page has been archived!")
            .setPositiveButton("View in Browser") { _, _ ->
                launchUrlInBrowser(url!!, getApplication())
            }
            .setNeutralButton("View in Reader") { _, _ ->
                launchUrlInReader(url!!, binding)
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}