package com.example.archivevn.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.example.archivevn.R
import com.example.archivevn.databinding.ActivityMainBinding
import com.example.archivevn.viewmodel.MainViewModel
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dialogHandler: DialogHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = MainViewModel(application)
        dialogHandler = DialogHandler(mainViewModel, supportFragmentManager)
        mainViewModel.initializeNotificationChannel()
        mainViewModel.setFragmentManager(supportFragmentManager)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this
        initializeBackPressDispatcher()

        setupViews()

        setupObservers()

        setupColdStartIntent()

        setupFirstTimeLaunchIntro()

        establishHiddenActionBar()

        keyboardListener()

        lineAnimationTheme()
    }

    private fun setupViews() {
        binding.goButton.goButtonAnimation.setOnClickListener {
            val url = binding.urlEditText.text.toString()
            mainViewModel.onGoButtonClicked(url)
        }

        binding.readerButton.setOnClickListener {
            val url = binding.urlEditText.text.toString()
            mainViewModel.onReaderButtonClicked(url)
        }
    }

    private fun setupObservers() {
        mainViewModel.pasteText.observe(this) { text ->
            if (!text.isNullOrBlank()) {
                binding.urlEditText.setText(text)
            }
        }

        mainViewModel.isUrlEditTextEnabled.observe(this) { isEnabled ->
            binding.urlEditText.isEnabled = isEnabled
        }

        mainViewModel.urlText.observe(this) { urlText ->
            binding.urlEditText.setText(urlText)
        }

        mainViewModel.isPasteButtonEnabled.observe(this) { isEnabled ->
            binding.pasteButton.isEnabled = isEnabled
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

        // Observe the HistoryFragment LiveData object to show/hide the history fragment
        mainViewModel.historyFragmentVisible.observe(this) { visible ->
            val historyFragment = supportFragmentManager.findFragmentByTag("HistoryFragment")
            if (visible && historyFragment == null) {
                val newHistoryFragment = HistoryFragment(mainViewModel)
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.reader_slide_up, 0, 0, R.anim.reader_slide_down)
                    .add(R.id.fragmentContainerViewHistory, newHistoryFragment, "HistoryFragment")
                    .addToBackStack("HistoryFragment")
                    .commit()
            } else if (visible && historyFragment != null) {
                supportFragmentManager.popBackStack(
                    "HistoryFragment",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }

        // Observe the ReaderFragment LiveData object to show/hide the reader fragment wheel
        mainViewModel.readerFragmentVisible.observe(this) { visiblePair ->
            val historyFragment = supportFragmentManager.findFragmentByTag("HistoryFragment")
            val readerFragment = supportFragmentManager.findFragmentByTag("ReaderFragment")
            if (historyFragment != null) {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }
                launchReaderFragment(visiblePair.second)
            } else if (readerFragment != null) {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.reader_slide_up, 0, 0, R.anim.reader_slide_down)
                    .show(readerFragment).commit()
            } else {
                launchReaderFragment(visiblePair.second)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v("MainActivityTag", "onNewIntent")
        mainViewModel.handleShareSheetUrlInBackground(intent)
    }

    private fun setupColdStartIntent() {
        val intent = intent
        if (intent != null) {
            // handle the intent
            val url = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!url.isNullOrEmpty()) {
                mainViewModel.launchUrlInBackground(url)
            }
        }
    }

    private fun launchReaderFragment(url: String? = null) {
        var defaultUrl = url
        if (url.isNullOrEmpty()) {
            defaultUrl = "https://archive.today"
            val newReaderFragment = ReaderFragment(defaultUrl)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.reader_slide_up, 0, 0, R.anim.reader_slide_down)
                .add(R.id.fragmentContainerView, newReaderFragment, "ReaderFragment")
                .addToBackStack("ReaderFragment")
                .commit()
        } else if (url.isNotEmpty() && !url.matches("^(http://|https://).*".toRegex())) {
            Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
        } else {
            val newReaderFragment = ReaderFragment(defaultUrl!!)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.reader_slide_up, 0, 0, R.anim.reader_slide_down)
                .add(R.id.fragmentContainerView, newReaderFragment, "ReaderFragment")
                .addToBackStack("ReaderFragment")
                .commit()
        }
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

    private fun establishHiddenActionBar() {
        // Set the custom layout for the action bar
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setCustomView(R.layout.custom_action_bar)
            elevation = 0F
        }
    }

    private fun toggleActionBar() {
        val actionBar = supportActionBar
        if (actionBar?.isShowing == true) {
            actionBar.hide()
        } else {
            actionBar?.show()
        }
    }

    private fun keyboardListener() {
        KeyboardVisibilityEvent.setEventListener(this
        ) { isOpen ->
            val layoutParams = binding.urlEditText.layoutParams as ConstraintLayout.LayoutParams
            if (isOpen) {
                layoutParams.matchConstraintPercentHeight = 1.0f
                toggleActionBar()
            } else {
                layoutParams.matchConstraintPercentHeight = 0.7f
                toggleActionBar()
            }
            binding.urlEditText.layoutParams = layoutParams
        }
    }


    private fun initializeBackPressDispatcher() {
        val dispatcher = onBackPressedDispatcher
        dispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            val dialogHandler = DialogHandler(mainViewModel, supportFragmentManager)
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else if (mainViewModel.archiveProgressLoading.value == true) {
                    dialogHandler.showArchiveInProgressDialog()
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupFirstTimeLaunchIntro() {
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

