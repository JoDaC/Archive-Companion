package com.example.archivevn.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.R
import com.example.archivevn.data.network.HtmlExtractionService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ReaderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val PASSED_URL = "url1"

class ReaderFragment : Fragment() {
    private var url: String? = null
    private lateinit var mWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(PASSED_URL)
        }
        Log.i("PASSED_URL_TAG", url!!)
    }

//    @SuppressLint("SetJavaScriptEnabled")
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_reader, container, false)
//        val loader = OkHttpHandler(url!!)
//        mWebView = view.findViewById(R.id.webview)
//        mWebView.webViewClient = WebViewClient()
//        val webSettings = mWebView.settings
//        webSettings.javaScriptEnabled = true
//        webSettings.textZoom = 150
//        MainScope().launch {
//            val extractedContent = loader.fetchExtractedTitleAndText(url!!)
//            val extractedTitle = extractedContent.second
//            val extractedText = extractedContent.first
//            Log.d("ReaderFragment", "Extracted Text: $extractedText")
//            val html = "<html><head></head><body>$extractedText</body></html>"
//            mWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
//        }
//        return view
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reader, container, false)
        val loader = OkHttpHandler(url!!)
        MainScope().launch {
            val extractedContent = loader.fetchExtractedTitleAndText(url!!)
            val extractedTitle = extractedContent.second
            val extractedText = extractedContent.first
            Log.d("ReaderFragment", "Extracted Text: $extractedText")
            val textView = view.findViewById<TextView>(R.id.text_display)
            textView.text = extractedText
        }
        return view
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