package com.example.archivevn.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

private const val PASSED_URL = ""

class ReaderFragment : Fragment() {
    private var url: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentReaderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reader, container, false)
        val loader = OkHttpHandler(url!!)
        MainScope().launch {
            // the following 3 lines need to be moved out of the reader fragment
            val extractedContent = loader.fetchExtractedTitleAndText(url!!)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(PASSED_URL)
        }
        Log.i("PASSED_URL_TAG", url!!)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param url URL parameter.
         * @return A new instance of fragment ReaderFragment.
         */
        @JvmStatic
        fun newInstance(url: String) =
            ReaderFragment().apply {
                arguments = Bundle().apply {
                    putString(PASSED_URL, url)
                }
            }
    }
}