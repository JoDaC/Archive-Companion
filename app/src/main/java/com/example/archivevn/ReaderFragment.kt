package com.example.archivevn

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PASSED_URL = "url1"

/**
 * A simple [Fragment] subclass.
 * Use the [ReaderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        // not sure if I need this
        container?.removeAllViews()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reader, container, false)
        // Scrape the text here and store it in a variable
        val loader = OkHttpHandler(url!!)
        Log.i("PASSED_URL_TAG_2", url!!)
        var scrapedText = ""
        MainScope().launch {
            scrapedText = loader.loadUrlAndParseToString()
            Log.i("scraped_text_tag", scrapedText)
            // Find the TextView in the layout
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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(url: String) =
            ReaderFragment().apply {
                arguments = Bundle().apply {
                    putString(PASSED_URL, url)
                }
            }
    }
}