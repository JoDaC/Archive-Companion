package com.example.archivevn.view

import android.content.Intent
import android.content.res.Configuration
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
    private var isEnabled = true
    private var visibility = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = MainViewModel(application, binding)
        mainViewModel.initializeNotificationChannel()
        mainViewModel.setFragmentManager(supportFragmentManager)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this
        initializeBackPressDispatcher()

        // Set onClickListener for Settings button to launch reader settings.
        binding.settingsButton.setOnClickListener {
            isEnabled = !isEnabled
            mainViewModel.onSettingsButtonClicked(isEnabled)
        }

        // Observe the isLoading LiveData object to show/hide the loading wheel
        mainViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe the archiveProgress LiveData object to the progress indicator
        mainViewModel.archiveProgressLoading.observe(this) { archiveProgressLoading ->
            binding.progressView.root.visibility =
                if (archiveProgressLoading) View.VISIBLE else View.GONE
        }

        // Set line animation depending on light/dark theme
        lineAnimationTheme()

        // Handle app launch via intent on cold start.
        val intent = intent
        if (intent != null) {
            mainViewModel.handleShareSheetUrlInBackground(intent)
        }
        // Launch App Intro carousel on first time launch.
        appIntroductionCarousel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v("MainActivityTag", "onNewIntent")
        mainViewModel.handleShareSheetUrlInBackground(intent)
    }

    private fun lineAnimationTheme() {
        val nightLine = binding.horizontalLineViewNight
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            nightLine.visibility = View.VISIBLE
        } else {
            nightLine.visibility = View.GONE
        }
    }

    private fun initializeBackPressDispatcher() {
        val dispatcher = onBackPressedDispatcher
        dispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                    binding.fragmentContainerView.visibility = View.GONE
                    binding.fragmentContainerViewHistory.visibility = View.GONE
                } else {
                    finish()
                }
            }
        })
    }

    private fun appIntroductionCarousel() {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            val editor = prefs.edit()
            editor.putBoolean("isFirstLaunch", false)
            editor.apply()

            val intent = Intent(this, AppIntroduction::class.java)
            startActivity(intent)
        }
    }
}

