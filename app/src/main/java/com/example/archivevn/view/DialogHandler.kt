package com.example.archivevn.view

import androidx.fragment.app.FragmentManager
import com.example.archivevn.viewmodel.MainViewModel

class DialogHandler(mainViewModel: MainViewModel, private val fragmentManager: FragmentManager) {

    private val dialogFragment = ArchiveDialogFragment(mainViewModel)

    fun showArchiveDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_1, url)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "archive_dialog")
    }

    fun showLinkFoundDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_2, url)
        dialogFragment.show(fragmentManager, "link_found_dialog")
    }

    fun showArchiveConfirmedDialog(url: String) {
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
