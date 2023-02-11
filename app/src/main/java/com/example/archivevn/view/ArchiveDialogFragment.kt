package com.example.archivevn.view

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.archivevn.R
import com.example.archivevn.databinding.ActivityMainBinding
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

    fun setDialogType(dialogType: Int, url: String?) {
        this.dialogType = dialogType
        this.url = url
    }

    fun setMainViewModel(mainViewModel: MainViewModel) {
        Log.d("ArchiveDialogFragment", "mainViewModel: $mainViewModel")
        this.mainViewModel = mainViewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("ArchiveDialogFragment", "dialogType: $dialogType")
        Log.d("ArchiveDialogFragment", "url: $url")
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
                        mainViewModel.launchUrlInBrowser("https://archive.vn/$url!!")
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
                        mainViewModel.launchUrlInBrowser(latestArchiveUrl)
                    }
                    .setNeutralButton("Launch in Reader") { _, _ ->
                        Log.i("linkToSendFragment", latestArchiveUrl)
                        // launch code for text extraction
                        mainViewModel.launchUrlInReader(latestArchiveUrl)
                    }
                return builder.create()
            }
            DIALOG_TYPE_3 -> {
                Log.i(tag, "archiveConfirmedDialog() started")
                val builder = AlertDialog.Builder(requireContext()).apply { }
                    .setTitle("Page has been archived!")
                    .setPositiveButton("View in Browser") { _, _ ->
                        mainViewModel.launchUrlInBrowser(url!!)
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
}