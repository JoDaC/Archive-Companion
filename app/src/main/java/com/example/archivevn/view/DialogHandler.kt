package com.example.archivevn.view

import androidx.fragment.app.FragmentManager
import com.example.archivevn.viewmodel.MainViewModel

/**
 * A class that handles the display of various types of dialogs related to archive actions.
 *
 * @param mainViewModel an instance of [MainViewModel] that contains necessary data for the archive actions.
 * @param fragmentManager the [FragmentManager] used to display the dialog fragments.
 */
class DialogHandler(mainViewModel: MainViewModel, private val fragmentManager: FragmentManager) {

    private val dialogFragment = ArchiveDialogFragment(mainViewModel)

    /**
     * Displays an archive dialog with the given [url].
     *
     * @param url the URL of the archive to be displayed in the dialog.
     */
    fun showArchiveDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_1, url)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "archive_dialog")
    }

    /**
     * Displays a "link found" dialog with the given [url].
     *
     * @param url the URL of the found link to be displayed in the dialog.
     */
    fun showLinkFoundDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_2, url)
        dialogFragment.show(fragmentManager, "link_found_dialog")
    }

    /**
     * Displays an archive confirmation dialog with the given [url].
     *
     * @param url the URL of the archive to be confirmed in the dialog.
     */
    fun showArchiveConfirmedDialog(url: String) {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_3, url)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "archive_confirmed_dialog")
    }

    /**
     * Displays an archive in-progress dialog.
     */
    fun showArchiveInProgressDialog() {
        dialogFragment.setDialogType(ArchiveDialogFragment.DIALOG_TYPE_4)
        dialogFragment.isCancelable = false
        dialogFragment.show(fragmentManager, "archive_in_progress_dialog")
    }
}
