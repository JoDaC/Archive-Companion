package com.example.archivevn.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.archivevn.R
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment

class AppIntroduction : AppIntro() {

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showStatusBar(true)
        isColorTransitionsEnabled = true
        // Ask for required NOTIFICATION permission on the third slide.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!notificationManager.areNotificationsEnabled()) {
                askForPermissions(
                    permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    slideNumber = 3,
                    required = true
                )
            }
        } else {
            // Earlier versions of Android
            askForPermissions(
                permissions = arrayOf(Manifest.permission_group.NOTIFICATIONS),
                slideNumber = 3,
                required = true
            )
        }
        addSlide(
            AppIntroFragment.createInstance(
                title = "Welcome to the Archive.vn Companion!",
                description = "This is the first slide of the example",
                imageDrawable = R.mipmap.black_a_transformed,
                backgroundColorRes = R.color.black,
                titleColorRes = R.color.white,
                descriptionColorRes = R.color.white,
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "...Let's get started!",
                description = "This is the last slide, I won't annoy you more :)",
                backgroundColorRes = R.color.white,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
            )

        )
        if (!notificationManager.areNotificationsEnabled()) {
            addSlide(
                AppIntroFragment.createInstance(
                    title = "Notification Permission",
                    description = "This is the last slide, I won't annoy you more :)",
                    backgroundColorRes = R.color.dark_greyish_blue,
                    titleColorRes = R.color.white,
                    descriptionColorRes = R.color.white,
                )
            )
        }
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        finish()
    }
}