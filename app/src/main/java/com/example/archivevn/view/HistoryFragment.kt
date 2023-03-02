package com.example.archivevn.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.archivevn.R
import com.example.archivevn.databinding.FragmentHistoryBinding
import com.example.archivevn.view.adapters.HistoryAdapter
import com.example.archivevn.viewmodel.MainViewModel


class HistoryFragment(mainViewModel: MainViewModel) : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private var mainViewModel: MainViewModel

    init {
        this.mainViewModel = mainViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)

        // Create the history adapter and set it on the RecyclerView
        historyAdapter = HistoryAdapter(mainViewModel)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter

        // Observe the history LiveData in the MainViewModel and submit the list to the adapter
        mainViewModel.history.observe(viewLifecycleOwner) { history ->
            historyAdapter.submitList(history)
        }
        return binding.root
    }
}