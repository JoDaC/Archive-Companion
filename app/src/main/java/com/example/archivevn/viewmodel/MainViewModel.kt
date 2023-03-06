package com.example.archivevn.viewmodel

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.archivevn.data.ArchiveService
import com.example.archivevn.data.HistoryItem
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.data.notifications.NotificationHandler
import com.example.archivevn.view.AppIntroduction
import com.example.archivevn.view.ArchiveDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

private const val PREFS_NAME = "HistoryPrefs"
private const val HISTORY_KEY = "history"

class MainViewModel(application: Application) :
    AndroidViewModel(application) {

    private lateinit var notificationChannel: NotificationHandler.NotificationChannel
    private lateinit var fragmentManager: FragmentManager
    private var dialogFragment: ArchiveDialogFragment = ArchiveDialogFragment(this)
    private val tag = "MainActivityTag"
    private val _isLoading = MutableLiveData<Boolean>()
    val _archiveProgressLoading = MutableLiveData<Boolean>()
    private val _history = MutableLiveData<List<HistoryItem>>().apply {
        value = listOf(
            HistoryItem("Your archived pages will appear here.", "https://archive.today", false),
        )
    }
    private val sharedPreferences = application.getSharedPreferences(
        "com.example.archivevn.prefs",
        Context.MODE_PRIVATE
    )
    private var isHistoryFragmentVisible = false
    private var isReaderFragmentCreated = false
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    val archiveProgressLoading: LiveData<Boolean>
        get() = _archiveProgressLoading
    val history: LiveData<List<HistoryItem>> = _history
    private val _pasteText = MutableLiveData<String?>()
    val pasteText: MutableLiveData<String?>
        get() = _pasteText
    private val _historyFragmentVisible = MutableLiveData<Boolean>()
    val historyFragmentVisible: MutableLiveData<Boolean>
        get() = _historyFragmentVisible
    private val _readerFragmentVisible = MutableLiveData<Pair<Boolean, String?>>()
    val readerFragmentVisible: MutableLiveData<Pair<Boolean, String?>>
        get() = _readerFragmentVisible

    private val _urlText = MutableLiveData<String>()
    val urlText: LiveData<String>
        get() = _urlText

    private val _isPasteButtonEnabled = MutableLiveData<Boolean>()
    val isPasteButtonEnabled: LiveData<Boolean>
        get() = _isPasteButtonEnabled

    private val _isUrlEditTextEnabled = MutableLiveData(true)
    val isUrlEditTextEnabled: LiveData<Boolean>
        get() = _isUrlEditTextEnabled

    init {
        _isLoading.value = false
        loadHistoryItemsFromPrefs()
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
    fun onGoButtonClicked(url: String) {
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
        val intent = Intent(getApplication(), AppIntroduction::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }


    /**
     * Handles the clipboard management when the "Paste" button is clicked.
     */
    fun onPasteButtonClicked() {
        val clipboardManager =
            getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
        if (!clipData.isNullOrBlank()) {
            _pasteText.value = clipData
        }
    }

    fun onHistoryButtonClicked() {
        setHistoryFragmentVisible(!isHistoryFragmentVisible)
    }

    fun onReaderButtonClicked(url: String? = null) {
        setReaderFragmentVisible(true, url)
    }

    fun inHistoryInReaderModeClick(historyItem: String) {
        setReaderFragmentVisible(true, historyItem)
    }

    fun inHistoryBrowserClick(historyItem: String) {
        Log.i("historyItemUrl", historyItem)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(historyItem))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(browserIntent)
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
        viewModelScope.launch {
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
                    _isLoading.value = false
                    _urlText.value = url
                    _isPasteButtonEnabled.value = false
                    _isUrlEditTextEnabled.value = false
                    _archiveProgressLoading.value = true
                    val archiveServiceIntent = Intent(getApplication(), ArchiveService::class.java)
                    startForegroundService(getApplication(), archiveServiceIntent)
                    val archivedResult = ArchiveService().archiveUrlInBackground(url).first
                    val articleTitle = ArchiveService().archiveUrlInBackground(url).second
                    addHistoryItem(articleTitle!!, url, false)
                    _archiveProgressLoading.value = false
                    _urlText.value = ""
                    _isUrlEditTextEnabled.value = true
                    NotificationHandler(getApplication()).showArchivalCompleteNotification()
                    showArchiveConfirmedDialog(archivedResult)
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

    private fun setHistoryFragmentVisible(visible: Boolean) {
        _historyFragmentVisible.value = visible
    }

    fun setReaderFragmentVisible(visible: Boolean, url: String? = null) {
        _readerFragmentVisible.value = Pair(visible, url)
    }

    private fun loadHistoryItemsFromPrefs() {
        val serializedList = sharedPreferences.getString(HISTORY_KEY, null)
        if (serializedList != null) {
            val typeToken = object : TypeToken<List<HistoryItem>>() {}.type
            _history.value = Gson().fromJson(serializedList, typeToken)
        }
    }

    private fun addHistoryItem(title: String, url: String, isReaderMode: Boolean) {
        val newItem = HistoryItem(title, url, isReaderMode)
        val currentList = _history.value ?: emptyList()
        val updatedList = listOf(newItem) + currentList
        _history.value = updatedList

        // Save updated list in SharedPreferences
        val editor = sharedPreferences.edit()
        val serializedList = Gson().toJson(updatedList)
        editor.putString(HISTORY_KEY, serializedList)
        editor.apply()
    }

    private fun showArchiveDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_1, url)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "archive_dialog")
    }

    private fun showLinkFoundDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_2, url)
        dialogFragment.show(fragmentManager, "link_found_dialog")
    }

    private fun showArchiveConfirmedDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_3, url)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "archive_confirmed_dialog")
    }

    fun showArchiveInProgressDialog() {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_4)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "archive_in_progress_dialog")
    }
}