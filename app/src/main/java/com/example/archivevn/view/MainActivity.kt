package com.example.archivevn.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.archivevn.R
import com.example.archivevn.databinding.ActivityMainBinding
import com.example.archivevn.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = MainViewModel(application, binding)
        mainViewModel.initializeNotificationChannel()
        mainViewModel.setFragmentManager(supportFragmentManager)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        // Initialize the Dialog Fragment
        val archiveDialogFragment = ArchiveDialogFragment()
        archiveDialogFragment.setMainViewModel(mainViewModel)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(archiveDialogFragment, "ArchiveDialogFragment")
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
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v("MainActivityTag", "onNewIntent")
        mainViewModel.handleShareSheetUrlInBackground(intent)
    }
}
