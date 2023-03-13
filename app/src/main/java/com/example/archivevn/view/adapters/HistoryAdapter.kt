package com.example.archivevn.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.archivevn.R
import com.example.archivevn.data.HistoryItem
import com.example.archivevn.databinding.ItemHistoryBinding
import com.example.archivevn.databinding.ItemHistorySwipeBinding
import com.example.archivevn.viewmodel.MainViewModel

private const val ITEM = 1
private const val SWIPE_MENU = 2

class HistoryAdapter(private val viewModel: MainViewModel) :
    ListAdapter<HistoryItem, RecyclerView.ViewHolder>(HistoryDiffCallback()) {

    private var swipePosition = -1

    private fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    override fun getItemViewType(position: Int): Int {
        return if (position == swipePosition) {
            SWIPE_MENU
        } else {
            ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == ITEM) {
            val binding: ItemHistoryBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.item_history, parent, false)
            ItemViewHolder(binding)
        } else {
            val menuBinding: ItemHistorySwipeBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.item_history_swipe, parent, false)
            MenuViewHolder(menuBinding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val historyItem = getItem(position)
        // For space between items
        when (holder) {
            is ItemViewHolder -> {
                val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
                layoutParams.bottomMargin = 1.dpToPx(holder.itemView.context)
                holder.itemView.layoutParams = layoutParams
                holder.bind(historyItem)
            }
            is MenuViewHolder -> {
                val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
                layoutParams.bottomMargin = 1.dpToPx(holder.itemView.context)
                holder.bind(historyItem, viewModel)
            }
        }
    }

    inner class SwipeController : Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(0, LEFT or RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return 0.1f
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            swipePosition = if (viewHolder.adapterPosition == swipePosition) {
                -1
            } else {
                viewHolder.adapterPosition
            }
            val view = viewHolder.itemView
            view.animate()
                .withStartAction{
                    view.translationX = if (direction == LEFT) view.width.toFloat() else -view.width.toFloat()
                    notifyItemChanged(swipePosition)
                }
                .withEndAction {
                    view.translationX = if (direction == LEFT) view.width.toFloat() else -view.width.toFloat()
                    notifyItemChanged(swipePosition)
                }
                .start()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val swipeController = SwipeController()
        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(recyclerView)
    }

    class ItemViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(historyItem: HistoryItem) {
            binding.historyItem = historyItem
            binding.historyTitleView.text = historyItem.title
            binding.subtitleTextView.text = historyItem.subtitle
            binding.urlTextView.text = historyItem.url
            binding.archivedUrlTextView.text = historyItem.archivedUrl
            binding.executePendingBindings()
        }
    }

    class MenuViewHolder(private val menuBinding: ItemHistorySwipeBinding) :
        RecyclerView.ViewHolder(menuBinding.root) {

        fun bind(historyItem: HistoryItem, viewModel: MainViewModel) {
            menuBinding.launchInReader.setOnClickListener {
                viewModel.inHistoryInReaderModeClick(historyItem.url)
            }
            menuBinding.launchOnWeb.setOnClickListener {
                viewModel.inHistoryBrowserClick(historyItem.url)
            }
            menuBinding.copyLink.setOnClickListener {
                viewModel.inHistoryCopyLink(historyItem.url)
            }
            menuBinding.shareSheet.setOnClickListener {
                viewModel.inHistoryShareLink(historyItem.url)
            }
            menuBinding.executePendingBindings()
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