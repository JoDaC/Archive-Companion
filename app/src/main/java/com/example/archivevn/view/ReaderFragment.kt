package com.example.archivevn.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.archivevn.data.network.OkHttpHandler
import com.example.archivevn.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(PASSED_URL)
        }
        Log.i("PASSED_URL_TAG", url!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reader, container, false)
        val loader = OkHttpHandler(url!!)
        Log.i("PASSED_URL_TAG_2", url!!)
        var scrapedText = ""
        MainScope().launch {
            scrapedText = loader.loadUrlAndParseToString()
            Log.i("scraped_text_tag", scrapedText)
            // Need to figure this part out
            val textView = view.findViewById<TextView>(R.id.text_display)
            // Set the text of the TextView to the scraped text
            textView.text = scrapedText
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