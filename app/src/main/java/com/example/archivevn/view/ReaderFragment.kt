package com.example.archivevn.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.archivevn.R
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.databinding.FragmentReaderBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ReaderFragment(private val url: String) :
    Fragment() {

    private lateinit var actionBar: ActionBar
    private var immersiveModeEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentReaderBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reader, container, false)
        val minimizeButton = binding.fragmentMinimizeButton
        val closeButton = binding.fragmentCloseButton
        minimizeButton.setOnClickListener {
            minimizeFragment()
        }
        closeButton.setOnClickListener {
            closeFragment()
        }
        setLoadingAnimationHeight(binding)
        // Set up the action bar
        actionBar = (requireActivity() as AppCompatActivity).supportActionBar!!

        val loader = OkHttpHandler(url)
        MainScope().launch {
            // the following 3 lines need to be moved out of the reader fragment
            val extractedContent = loader.fetchExtractedTitleAndText(url)
            val extractedTitle = extractedContent.second
            val extractedText = extractedContent.first

            Log.d("ReaderFragment", "Extracted Text: $extractedText")
            binding.textDisplay.text = extractedText
            binding.readerLoadingAnimation.visibility = View.GONE
            binding.textDisplay.visibility = View.VISIBLE
            binding.titleDisplay.text = extractedTitle
            binding.titleDisplay.visibility = View.VISIBLE
        }
        return binding.root
    }

    // Setting Immersive mode
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActionBar()
        val window = requireActivity().window
        val windowInsetsController = WindowCompat.getInsetsController(window, view)
        windowInsetsController.let {
            it.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showActionBar()
        val window = requireActivity().window
        val windowInsetsController = WindowCompat.getInsetsController(window, requireView())
        windowInsetsController.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && !immersiveModeEnabled) {
            hideActionBar()
            // Set immersive mode
            val window = requireActivity().window
            val windowInsetsController = WindowCompat.getInsetsController(window, requireView())
            windowInsetsController.let {
                it.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                it.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            immersiveModeEnabled = true
        } else if (hidden && immersiveModeEnabled) {
            showActionBar()
            // Exit immersive mode
            val window = requireActivity().window
            val windowInsetsController = WindowCompat.getInsetsController(window, requireView())
            windowInsetsController.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            immersiveModeEnabled = false
        }
    }

    private fun setLoadingAnimationHeight(binding: FragmentReaderBinding) {
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val dp = 300
        val px =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics)
                .toInt()
        val paddingDp = (height - px) / 2
        binding.readerLoadingAnimation.setPadding(0, paddingDp, 0, 0)
    }

    private fun hideActionBar() {
        actionBar.hide()
    }

    private fun showActionBar() {
        actionBar.show()
    }

    private fun minimizeFragment() {
        Log.d("ReaderFragment", "minimizeFragment() called")
        val readerFragment = parentFragmentManager.findFragmentByTag("ReaderFragment")
        if (readerFragment != null) {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.reader_slide_down,
                    R.anim.reader_slide_down,
                    R.anim.reader_slide_down,
                    R.anim.reader_slide_down
                )
                .hide(readerFragment).commit()
        }
        immersiveModeEnabled = true
    }

    private fun closeFragment() {
        parentFragmentManager.popBackStack()
    }
}