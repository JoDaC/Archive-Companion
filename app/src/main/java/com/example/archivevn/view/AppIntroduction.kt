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
        isWizardMode = true
        showStatusBar(true)
        isColorTransitionsEnabled = true
        setImmersiveMode()
        // Ask for required NOTIFICATION permission on the third slide.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!notificationManager.areNotificationsEnabled()) {
                askForPermissions(
                    permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    slideNumber = 5,
                    required = true
                )
            }
        } else {
            // Earlier versions of Android
            askForPermissions(
                permissions = arrayOf(Manifest.permission_group.NOTIFICATIONS),
                slideNumber = 5,
                required = true
            )
        }
        addSlide(
            AppIntroFragment.createInstance(
                title = "Welcome to the Archive.today Companion!",
                description = "Welcome to Archive.today Companion App! With this app, you'll be able to easily archive web pages and easily read them whenever you want.",
                imageDrawable = R.mipmap.black_a_transformed,
                titleTypefaceFontRes = R.font.space_grotesk,
                descriptionTypefaceFontRes = R.font.space_grotesk,
                backgroundColorRes = R.color.white,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "Your Personal Time Capsule For Web Pages",
                description = "Archive.today is a time capsule for web pages. It takes a 'snapshot' of a webpage that will always be online even if the original page disappears. It saves a text and graphical copy of the page and provides a short link to an unalterable record of any web page.",
                imageDrawable = R.mipmap.black_a_transformed,
                titleTypefaceFontRes = R.font.space_grotesk,
                descriptionTypefaceFontRes = R.font.space_grotesk,
                backgroundColorRes = R.color.intro_grey,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
            )

        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "Save, Read, and Bypass Paywalls!",
                description = " With this companion, you can easily create and access these time capsules using your mobile device, and even bypass article paywalls in some cases. We're the perfect companion for anyone who wants to keep a permanent record of pages when you're on the go!",
                imageDrawable = R.mipmap.hand_with_phone_edit,
                titleTypefaceFontRes = R.font.space_grotesk,
                descriptionTypefaceFontRes = R.font.space_grotesk,
                backgroundColorRes = R.color.intro_grey,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
            )

        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "How to Use!",
                description = "Simply share the URL of the page you want to save with the app, and we'll take care of the rest. Our app will archive the page and make it available for you to read whenever you want. Try it now!",
                titleTypefaceFontRes = R.font.space_grotesk,
                descriptionTypefaceFontRes = R.font.space_grotesk,
                backgroundColorRes = R.color.intro_grey,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
            )

        )
        if (!notificationManager.areNotificationsEnabled()) {
            addSlide(
                AppIntroFragment.createInstance(
                    title = "Notification Permission",
                    description = "This is the last slide, I won't annoy you more :)",
                    titleTypefaceFontRes = R.font.space_grotesk,
                    descriptionTypefaceFontRes = R.font.space_grotesk,
                    backgroundColorRes = R.color.intro_grey,
                    titleColorRes = R.color.black,
                    descriptionColorRes = R.color.black,
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