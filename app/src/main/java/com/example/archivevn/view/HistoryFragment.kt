package com.example.archivevn.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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


class HistoryFragment(private val mainViewModel: MainViewModel) : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private var originalStatusBarColor: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)

        // Create the history adapter and set it on the RecyclerView
        historyAdapter = HistoryAdapter(mainViewModel)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter

        val window = activity?.window
        originalStatusBarColor = window?.statusBarColor
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val backgroundColor: Int = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            Color.BLACK
        } else {
            Color.LTGRAY
        }
        val backgroundDrawable = ColorDrawable(backgroundColor)
        backgroundDrawable.alpha = 0
        binding.historyFragmentBackGround.background = backgroundDrawable

        val statusFadeAnim =
            ValueAnimator.ofArgb(window?.statusBarColor ?: Color.TRANSPARENT, backgroundColor)
        statusFadeAnim.duration = 1800
        statusFadeAnim.addUpdateListener { valueAnimator ->
            window?.statusBarColor = valueAnimator.animatedValue as Int
        }
        statusFadeAnim.start()

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

        // Observe the history LiveData in the MainViewModel and submit the list to the adapter
        mainViewModel.history.observe(viewLifecycleOwner) { history ->
            historyAdapter.submitList(history)
        }
        return binding.root
    }

    override fun onDestroyView() {
        val window = activity?.window
        val statusFadeAnim =
            ValueAnimator.ofArgb(window?.statusBarColor ?: Color.BLACK, originalStatusBarColor!!)
        statusFadeAnim.duration = 300
        statusFadeAnim.addUpdateListener { valueAnimator ->
            window?.statusBarColor = valueAnimator.animatedValue as Int
        }
        statusFadeAnim.start()
        super.onDestroyView()
    }
}