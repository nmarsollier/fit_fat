package com.nmarsollier.fitfat.ui.dashboard

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.uiUtils.closeKeyboard
import com.nmarsollier.fitfat.databinding.MainActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DashboardActivity : AppCompatActivity() {
    var currentTab: Screen = Screen.MEASURES_LIST

    private val binding by lazy {
        MainActivityBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        getViewModel<DashboardViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainScope().launch(Dispatchers.IO) {
            viewModel.reduce(DashboardAction.Initialize)
        }

        setContentView(binding.root)

        binding.navigation.setOnItemSelectedListener { item ->
            viewModel.reduce(
                DashboardAction.CurrentSelectedTab(
                    when (item.itemId) {
                        R.id.menu_options -> Screen.OPTIONS
                        R.id.menu_stats -> Screen.STATS
                        else -> Screen.MEASURES_LIST
                    }
                )
            )

            true
        }

        binding.pager.adapter = DashboardPageAdapter(supportFragmentManager)
        binding.pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                DashboardAction.CurrentSelectedTab(
                    when (position) {
                        0 -> Screen.OPTIONS
                        2 -> Screen.STATS
                        else -> Screen.MEASURES_LIST
                    }
                )
                invalidateOptionsMenu()
            }
        })

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                setCurrentSelectedTab(state.selectedTab)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentTab != Screen.MEASURES_LIST) {
                    viewModel.reduce(DashboardAction.CurrentSelectedTab(Screen.MEASURES_LIST))
                } else {
                    finish()
                }
            }
        })
    }

    private fun setCurrentSelectedTab(tab: Screen) {
        currentTab = tab

        closeKeyboard()

        binding.pager.currentItem = currentTab.ordinal

        binding.navigation.selectedItemId = when (currentTab) {
            Screen.OPTIONS -> R.id.menu_options
            Screen.STATS -> R.id.menu_stats
            Screen.MEASURES_LIST -> R.id.menu_home
        }

        setTitle(
            when (currentTab) {
                Screen.OPTIONS -> R.string.home_options_title
                Screen.STATS -> R.string.home_progress_title
                Screen.MEASURES_LIST -> R.string.home_measure_title
            }
        )
    }
}
