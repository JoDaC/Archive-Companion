package com.example.archivevn.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.archivevn.R
import com.example.archivevn.data.HistoryItem
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.data.notifications.NotificationHandler
import com.example.archivevn.databinding.ActivityMainBinding
import com.example.archivevn.view.AppIntroduction
import com.example.archivevn.view.ArchiveDialogFragment
import com.example.archivevn.view.HistoryFragment
import com.example.archivevn.view.ReaderFragment
import com.example.archivevn.view.adapters.HistoryAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val binding: ActivityMainBinding) :
    AndroidViewModel(application) {

    private lateinit var notificationChannel: NotificationHandler.NotificationChannel
    private lateinit var fragmentManager: FragmentManager
    private var dialogFragment: ArchiveDialogFragment = ArchiveDialogFragment(this)
    private val tag = "MainActivityTag"
    private val _isLoading = MutableLiveData<Boolean>()
    private val _archiveProgressLoading = MutableLiveData<Boolean>()
    private val _history = MutableLiveData<List<HistoryItem>>(emptyList())
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    val archiveProgressLoading: LiveData<Boolean>
        get() = _archiveProgressLoading
    val history: LiveData<List<HistoryItem>> = _history

    init {
//        this.dialogFragment.setMainViewModel(this)
        _isLoading.value = false
    }

    /**
     * Initializes a new notification channel for this app.
     */
    fun initializeNotificationChannel() {
        notificationChannel = NotificationHandler.NotificationChannel(getApplication())
        notificationChannel.createNotificationChannel()
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
     * Launches a new browser Intent from the ArchiveDialogFragment with the specified URL.
     */
    fun onGoButtonClicked() {
        val url = binding.urlEditText.text.toString()
        if (url.isNotEmpty()) {
            launchUrlInBackground(url)
        } else {
            Toast.makeText(getApplication(), "Please enter a URL", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handles the event when the "Intro" button is clicked. this is for testing puposes only.
     * Launches a new browser Intent from the ArchiveDialogFragment with the specified URL.
     */
    fun introButtonClicked() {
        val prefs =
            getApplication<Application>().getSharedPreferences("MyPrefs", Application.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)
        val intent = Intent(getApplication(), AppIntroduction::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }

    /**
     * Handles the event when the "Settings" button is clicked. this is for testing puposes only.
     * Launches a new browser Intent from the ArchiveDialogFragment with the specified URL.
     */
    fun onSettingsButtonClicked(isEnabled: Boolean) {
        binding.urlEditText.isEnabled = isEnabled
        if (binding.urlEditText.isEnabled) {
            binding.urlEditText.hint = getApplication<Application>().getString(R.string.enter_a_url_to_archive)
        } else {
            binding.urlEditText.hint =
                getApplication<Application>().getString(R.string.edit_text_page_archival_hint)
            Toast.makeText(
                getApplication(),
                "Text entry disabled during archival.",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        addHistoryItem("Sweet ass article title", "www.fuckgoogle.com", false)
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
            // delivering push notification here for testing purposes
//            NotificationHandler(getApplication()).showTestNotification()
            val readerFragment = ReaderFragment.newInstance("https://www.businesstoday.in/latest/story/board-should-fire-sundar-pichai-google-layoffs-trigger-anger-sorrow-disbelief-367018-2023-01-22")
            fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.reader_slide_up, 0, 0, R.anim.reader_slide_down)
                .replace(R.id.fragmentContainerView, readerFragment)
                .addToBackStack("ReaderFragment")
                .commit()
            binding.fragmentContainerView.visibility = View.VISIBLE
        }
    }

    /**
     * Handles the event when the "History" button is clicked.
     * Launches a new HistoryFragment.
     */
    fun onHistoryButtonClicked() {
        if (binding.fragmentContainerViewHistory.isVisible) {
            fragmentManager.popBackStack()
            binding.fragmentContainerViewHistory.visibility = View.GONE
        } else {
            val historyFragment = HistoryFragment(this)
            fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.reader_slide_up, 0, 0, R.anim.reader_slide_down)
                .replace(R.id.fragmentContainerViewHistory, historyFragment)
                .addToBackStack("HistoryFragment")
                .commit()
            binding.fragmentContainerViewHistory.visibility = View.VISIBLE
        }
    }

    fun onViewInReaderModeClick(historyItem: HistoryItem) {
        // This method is called when the user clicks the "Reader" button for a history item.
        // You can add logic here to open the selected page in reader mode.
        // You can use the Navigation component to navigate to the ReaderFragment.
    }

    fun onViewInBrowserClick(historyItem: HistoryItem) {
        // This method is called when the user clicks the "Browser" button for a history item.
        // You can add logic here to open the selected page in the default browser using an Intent.
        // For example:
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(historyItem.url))
        getApplication<Application>().startActivity(browserIntent)
    }

    private fun addHistoryItem(title: String, url: String, isReaderMode: Boolean) {
        // This method is called when a new history item is added to the list.
        // You should create a new HistoryItem instance with the given title, url, and isReaderMode flag,
        // and add it to the list of history items stored in the _history LiveData object.
        val newItem = HistoryItem(title, url, isReaderMode)
        val currentList = _history.value ?: emptyList()
        _history.value = listOf(newItem) + currentList
    }

    /**
     * Launches a new ReaderFragment with the specified URL.
     *
     * @param url The URL to parse and display in the reader.
     */
    fun launchUrlInReader(url: String) {
        Log.i("Shared URL %", url)
        val readerFragment = ReaderFragment.newInstance(url)
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.reader_slide_up, 0, 0, R.anim.reader_slide_down)
            .replace(R.id.fragmentContainerView, readerFragment)
            .addToBackStack("ReaderFragment")
            .commit()
        binding.fragmentContainerView.visibility = View.VISIBLE
    }

    /**
     * Launches a new background coroutine to check if the specified URL contains three possible words
     * or phrases to determine if a page is already archived or not. Will then call appropriate
     * AlertDialog to inform if the url is already archived, if not archived, or if the archival
     * process is complete.

     * If Archival is chosen, display a push notification to indicate archival is in progress.

     * @param url The URL to archive or check for archival.
     * @param urlToArchive A flag indicating whether to archive the page.
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
            _isLoading.value = true
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
                    _isLoading.value = false
                    binding.urlEditText.isEnabled = false
                    editTextHint()
                    _archiveProgressLoading.value = true
                    val archivedResult = loader.launchPageArchival(url)
                    Log.i("Final URL of Archived page ", archivedResult)
                    val articleTitle = loader.fetchExtractedTitleAndText(archivedResult).second
                    addHistoryItem(articleTitle!!, archivedResult, false)
                    NotificationHandler(getApplication()).showArchivalCompleteNotification()
                    _archiveProgressLoading.value = false
                    showArchiveConfirmedDialog(archivedResult)
                    val notificationChannel =
                        NotificationHandler.NotificationChannel(getApplication())
                    notificationChannel.closeNotification()
                    binding.urlEditText.isEnabled = false
                    editTextHint()
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Handles the URL when the app is launched via the Android share sheet.
     *
     * @param intent The intent containing the shared URL.
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

    private fun editTextHint() {
        if (binding.urlEditText.isEnabled) {
            binding.urlEditText.hint =
                getApplication<Application>().getString(R.string.enter_a_url_to_archive)
        } else {
            binding.urlEditText.hint =
                getApplication<Application>().getString(R.string.edit_text_page_archival_hint)

        }
    }

    private fun showArchiveDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_1, url)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "archive_dialog")
    }

    private fun showLinkFoundDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_2, url)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "link_found_dialog")
    }

    private fun showArchiveConfirmedDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_3, url)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "archive_confirmed_dialog")
    }
}