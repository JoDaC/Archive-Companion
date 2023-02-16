package com.example.archivevn.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.archivevn.R
import com.example.archivevn.databinding.ActivityMainBinding
import com.example.archivevn.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
//    private lateinit var clipboardManager: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = MainViewModel(application, binding)
        mainViewModel.initializeNotificationChannel()
        mainViewModel.setFragmentManager(supportFragmentManager)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        initializeBackPressDispatcher()

//        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//        val clipData = clipboardManager.primaryClip

        // Initialize the Dialog Fragment
        val archiveDialogFragment = ArchiveDialogFragment()
        archiveDialogFragment.setMainViewModel(mainViewModel)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.commit()

        // Set onClickListener for GO button.
        binding.goButton.setOnClickListener {
            mainViewModel.onGoButtonClicked()
        }
        // Set onClickListener for Reader button to launch Reader fragment.
        binding.readerButton.setOnClickListener {
            mainViewModel.onReaderButtonClicked()
        }
        // Set ProgressBar to no visibility.
        binding.progressBar.visibility = View.GONE

        // Set Paste button to no visibility onCreate.
        binding.pasteButton.visibility = View.GONE

        // Handle app launch via intent on cold start.
        val intent = intent
        if (intent != null) {
            mainViewModel.handleShareSheetUrlInBackground(intent)
        }

//        clipBoardListener(clipboardManager)
//        binding.pasteButton.setOnClickListener {
//            val pasteData = clipData?.getItemAt(0)
//            val text = pasteData?.text.toString()
//            mainViewModel.onPasteButtonClicked(text)
//        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v("MainActivityTag", "onNewIntent")
        mainViewModel.handleShareSheetUrlInBackground(intent)
    }

    private fun initializeBackPressDispatcher() {
        val dispatcher = onBackPressedDispatcher
        dispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

//    override fun onResume() {
//        super.onResume()
//        Log.i("MainActivityTag", "onResume()")
//        clipBoardListener(clipboardManager)
//    }
//
//    private fun clipBoardListener(clipboard: ClipboardManager) {
//        clipboard.addPrimaryClipChangedListener {
//            if (clipboard.primaryClip != null) {
//                binding.pasteButton.visibility = View.VISIBLE
//            }
//        }
//    }
}

