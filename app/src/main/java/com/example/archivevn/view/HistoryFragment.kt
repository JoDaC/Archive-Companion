package com.example.archivevn.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.archivevn.R
import com.example.archivevn.databinding.FragmentHistoryBinding
import com.example.archivevn.view.adapters.HistoryAdapter
import com.example.archivevn.viewmodel.MainViewModel


class HistoryFragment(private val mainViewModel: MainViewModel) : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var actionBar: ActionBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        // Set up the action bar
        actionBar = (requireActivity() as AppCompatActivity).supportActionBar!!

        // Create the history adapter and set it on the RecyclerView
        historyAdapter = HistoryAdapter(mainViewModel)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter

        // Observe the history LiveData in the MainViewModel and submit the list to the adapter
        mainViewModel.history.observe(viewLifecycleOwner) { history ->
            historyAdapter.submitList(history)
        }

        changeBackgroundAndStatusColor()

        return binding.root
    }

    private fun changeBackgroundAndStatusColor () {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val backgroundColor: Int = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            resources.getColor(R.color.dark_theme_grey, null)
        } else {
            resources.getColor(R.color.light_theme_grey, null) }
        val backgroundDrawable = ColorDrawable(backgroundColor)
        backgroundDrawable.alpha = 0
        binding.historyFragmentBackGround.background = backgroundDrawable

        Handler(Looper.getMainLooper()).postDelayed({
            // Fade in the opacity of the background drawable
            val fadeAnim = ValueAnimator.ofInt(0, 255)
            fadeAnim.duration = 1000
            fadeAnim.addUpdateListener { valueAnimator ->
                val alpha = valueAnimator.animatedValue as Int
                backgroundDrawable.alpha = alpha
                binding.historyFragmentBackGround.background = backgroundDrawable
            }
            fadeAnim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    binding.historyFragmentBackGround.background.alpha = 255
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            fadeAnim.start()
        }, 500)
    }
}