package com.example.archivevn.viewmodel

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
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.R
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    private lateinit var notificationChannel: NotificationHandler.NotificationChannel

    fun onCreate() {
        notificationChannel = NotificationHandler.NotificationChannel(this)
        notificationChannel.createNotificationChannel()
    }

    fun goButtonSearch() {
        val url = urlEditText.text.toString()
        if (url.isNotEmpty()) {
            launchUrlInBrowser(url)
        } else {
            Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchUrlInBrowser(url: String) {
        // Implementation for launching URL in browser
    }

    fun launchUrlInReader(url: String) {
        // Implementation for launching URL in reader
    }

    fun launchUrlInBackground(url: String) {
        // Implementation for launching URL in background
    }

    fun handleShareSheetUrlInBrowser(intent: Intent?) {
        // Implementation for handling share sheet URL in browser
    }

    fun handleShareSheetUrlInBackground(intent: Intent?) {
        // Implementation for handling share sheet URL in background
    }

    fun showTestNotification() {
        // Implementation for showing test notification
    }

    fun closeNotification() {
        // Implementation for closing notification
    }
}