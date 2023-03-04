package com.example.archivevn.view

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.archivevn.R
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.databinding.FragmentReaderBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.example.archivevn.viewmodel.MainViewModel

private const val PASSED_URL = ""

class ReaderFragment(private val mainViewModel: MainViewModel, private val url: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentReaderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reader, container, false)

        // Find the minimize and close buttons in the layout
        val minimizeButton = binding.fragmentMinimizeButton
        val closeButton = binding.fragmentCloseButton

        // Set click listeners for the buttons
        minimizeButton.setOnClickListener {
            // Minimize the fragment
            minimizeFragment()
        }
        closeButton.setOnClickListener {
            // Close the fragment
            closeFragment()
        }

        setLoadingAnimationHeight(binding)


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
        val window = requireActivity().window
        val windowInsetsController = WindowCompat.getInsetsController(window, view)
        windowInsetsController.let {
            it.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onPause() {
        super.onPause()
        // Setting Immersive mode onPause
        val window = requireActivity().window
        val windowInsetsController = WindowCompat.getInsetsController(window, requireView())
        windowInsetsController .show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
    }
    override fun onResume() {
        super.onResume()
        // Setting Immersive mode onResume
        val window = requireActivity().window
        val windowInsetsController = WindowCompat.getInsetsController(window, requireView())
        windowInsetsController.let {
            it.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//    }

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

    private fun minimizeFragment() {
//        parentFragmentManager.clearBackStack("HistoryFragment")
        // Animate the closing of the fragmentContainerView
       mainViewModel.binding.fragmentContainerView.animate()
            .translationY(mainViewModel.binding.fragmentContainerView.height.toFloat())
            .setDuration(500)
            .withEndAction {
                // Set the fragmentContainerView to GONE
                mainViewModel.binding.fragmentContainerView.visibility = View.GONE
            }
            .start()
    }

    private fun closeFragment() {
        parentFragmentManager.popBackStack()
    }
}