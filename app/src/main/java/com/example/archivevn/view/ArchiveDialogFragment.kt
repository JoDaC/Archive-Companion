package com.example.archivevn.view

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.archivevn.viewmodel.MainViewModel


class ArchiveDialogFragment() : DialogFragment() {
    private lateinit var mainViewModel: MainViewModel

    private var dialogType: Int = 0
    private var url: String? = null

    companion object {
        const val DIALOG_TYPE_1 = 1
        const val DIALOG_TYPE_2 = 2
        const val DIALOG_TYPE_3 = 3
    }

    /**
    * Sets the type of dialog to be displayed and a URL to be used in certain cases.
    * @param dialogType The type of dialog to be displayed.
    * @param url A URL to be used in certain cases.
    */
    fun setDialogType(dialogType: Int, url: String?) {
        this.dialogType = dialogType
        this.url = url
    }

    /**
     *Sets the MainViewModel object for this fragment.
     *@param mainViewModel The MainViewModel object to set for this fragment.
     */
    fun setMainViewModel(mainViewModel: MainViewModel) {
        this.mainViewModel = mainViewModel
    }

    /**
     * Creates and returns a new Dialog object based on the specified dialog type.
     * @return A new Dialog object.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        when (dialogType) {
            DIALOG_TYPE_1 -> {
                Log.i(tag, "First Time archiveDialog() started")
                val builder = AlertDialog.Builder(requireContext()).apply { }
                    .setTitle("No Archived Page Found")
                    .setMessage("Do you want to archive this page?")
                    .setPositiveButton("Yes") { _, _ ->
                        mainViewModel.launchUrlInBackground(url!!, true)
                    }
                    .setNegativeButton("No") { _, _ ->
                    }
                    .setNeutralButton("Launch in Browser") { _, _ ->
                        launchUrlInBrowser("https://archive.vn/$url!!")
                    }
                return builder.create()
            }
            DIALOG_TYPE_2 -> {
                Log.i(tag, "linkFoundDialog() started")
                val latestArchiveUrl = "http://archive.is/newest/$url"
                val builder = AlertDialog.Builder(requireContext()).apply { }
                    .setTitle("Archived Page for this URL has been found")
                    .setMessage("Do you want to view in your browser or read now?")
                    .setPositiveButton("Launch in Browser") { _, _ ->
                        launchUrlInBrowser(latestArchiveUrl)
                    }
                    .setNeutralButton("Launch in Reader") { _, _ ->
                        Log.i("linkToSendFragment", latestArchiveUrl)
                        mainViewModel.launchUrlInReader(latestArchiveUrl)
                    }
                return builder.create()
            }
            DIALOG_TYPE_3 -> {
                Log.i(tag, "archiveConfirmedDialog() started")
                val builder = AlertDialog.Builder(requireContext()).apply { }
                    .setTitle("Page has been archived!")
                    .setPositiveButton("View in Browser") { _, _ ->
                        launchUrlInBrowser(url!!)
                    }
                    .setNeutralButton("View in Reader") { _, _ ->
                        mainViewModel.launchUrlInReader(url!!)
                    }
                return builder.create()
            }
            else -> {
                return super.onCreateDialog(savedInstanceState)
            }
        }
    }

    /**
     * Launches a new browser Intent with the specified URL and optionally amends the given url for
     * archive.vn or archive.is if specified.
     *
     * @param url The URL to launch in the browser.
     * @param urlToArchive A flag indicating whether to amend the url for archive.vn or archive.is.
     */
    fun launchUrlInBrowser(url: String, urlToArchive: Boolean? = null) {
        Log.i("Shared URL %", url)
        var archiveUrl = "https://archive.vn/$url"
        if (urlToArchive == true) {
            archiveUrl = "https://archive.is/?$url"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}