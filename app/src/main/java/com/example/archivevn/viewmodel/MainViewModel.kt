package com.example.archivevn.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import com.example.archivevn.R
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.data.notifications.NotificationHandler
import com.example.archivevn.databinding.ActivityMainBinding
import com.example.archivevn.view.ArchiveDialogFragment
import com.example.archivevn.view.ReaderFragment
import kotlinx.coroutines.*

class MainViewModel(application: Application, private val binding: ActivityMainBinding) :
    AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val mContext = ContextThemeWrapper(application, R.style.Theme_Archivevn)

    private lateinit var notificationChannel: NotificationHandler.NotificationChannel
    private lateinit var fragmentManager: FragmentManager
    private var dialogFragment: ArchiveDialogFragment = ArchiveDialogFragment()
    private val tag = "MainActivityTag"

    init {
        this.dialogFragment.setMainViewModel(this)
    }

    /**
     * Initializes a new notification channel for this app.
     */
    fun initializeNotificationChannel() {
        notificationChannel = NotificationHandler.NotificationChannel(getApplication())
    }

    /**
     * Sets the fragment manager for this view model.
     *
     * @param fragmentManager The fragment manager to set.
     */
    fun setFragmentManager(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
    }

    /**
     * Handles the event when the "Go" button is clicked.
     *  Launches a new browser Intent from the ArchiveDialogFragment with the specified URL.
     */
    fun onGoButtonClicked() {
        val url = binding.urlEditText.text.toString()
        if (url.isNotEmpty()) {
            dialogFragment.launchUrlInBrowser(url)
        } else {
            Toast.makeText(getApplication(), "Please enter a URL", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handles the event when the "Reader" button is clicked.
     * Launches a new ReaderFragment by passing the specified URL to launchUrlInReader().
     */
    fun onReaderButtonClicked() {
        val url = binding.urlEditText.text.toString()
        if (url.isNotEmpty()) {
            launchUrlInReader(url)
        } else {
            Toast.makeText(
                getApplication(),
                "Please enter a URL to view in Reader",
                Toast.LENGTH_SHORT
            )
                .show()
            // delivering push notification here for easy testing purposes
            NotificationHandler(getApplication()).showTestNotification()
            SystemClock.sleep(5000)
            notificationChannel.closeNotification()
        }
    }

    /**
    Launches a new ReaderFragment with the specified URL.

    @param url The URL to parse and display in the reader.
     */
    fun launchUrlInReader(url: String) {
        Log.i("Shared URL %", url)
        val readerFragment = ReaderFragment.newInstance(url)
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, readerFragment)
            .commit()
        binding.fragmentContainerView.visibility = View.VISIBLE
    }

    /**
    Launches a new background coroutine to check if the specified URL contains three possible words
    or phrases to determine if a page is already archived or not. Will then call appropriate
    AlertDialog to inform if the url is already archived, if not archived, or if the archival
    process is complete.

    If Archival is chosen, display a push notification to indicate archival is in progress.

    @param url The URL to archive or check for archival.
    @param urlToArchive A flag indicating whether to archive the page.
     */
    fun launchUrlInBackground(
        url: String,
        urlToArchive: Boolean? = null
    ) {
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
                    Log.i(tag, "Displaying showArchive Dialog")
                    showArchiveDialog(url)
                }
                "Newest" -> {
                    Log.i(tag, "Displaying showLinkFoundDialog")
                    showLinkFoundDialog(url)
                }
                "My url is alive and I want to archive its content" -> {
                    Log.i(tag, "Triggering page archival and displaying showArchiveConfirmedDialog")
                    NotificationHandler(getApplication()).showLoadingNotification()
                    val archivedResult = loader.launchPageArchival(url)
                    Log.i("Final URL of Archived page ", archivedResult)
                    showArchiveConfirmedDialog(archivedResult)
                    val notificationChannel =
                        NotificationHandler.NotificationChannel(getApplication())
                    notificationChannel.closeNotification()
                }
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    /**
    Handles the URL when the app is launched via the Android share sheet.

    @param intent The intent containing the shared URL.
     */
    fun handleShareSheetUrlInBackground(intent: Intent?) {
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

    private fun showArchiveDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_1, url)
        dialogFragment.show(fragmentManager, "archive_dialog")
    }
    private fun showLinkFoundDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_2, url)
        dialogFragment.show(fragmentManager, "link_found_dialog")
    }
    private fun showArchiveConfirmedDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_3, url)
        dialogFragment.show(fragmentManager, "archive_confirmed_dialog")
    }
}