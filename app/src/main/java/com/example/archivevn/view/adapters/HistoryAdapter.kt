package com.example.archivevn.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.archivevn.R
import com.example.archivevn.data.HistoryItem
import com.example.archivevn.databinding.ItemHistoryBinding
import com.example.archivevn.viewmodel.MainViewModel

class HistoryAdapter(private val viewModel: MainViewModel) :
    ListAdapter<HistoryItem, HistoryAdapter.ViewHolder>(HistoryDiffCallback()) {

    private fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemHistoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_history, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyItem = getItem(position)
        // Add spacing between items
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.bottomMargin =
            16.dpToPx(holder.itemView.context) // set the desired spacing in pixels
        holder.itemView.layoutParams = layoutParams
        holder.bind(historyItem, viewModel)
    }

    class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(historyItem: HistoryItem, viewModel: MainViewModel) {
            binding.historyItem = historyItem
            binding.historyTitleView.text = historyItem.title
            binding.urlTextView.text = historyItem.url
            binding.launchInReader.setOnClickListener {
                viewModel.onViewInReaderModeClick(historyItem)
            }
            binding.launchOnWeb.setOnClickListener {
                viewModel.onViewInBrowserClick(historyItem)
            }
            binding.executePendingBindings()
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}