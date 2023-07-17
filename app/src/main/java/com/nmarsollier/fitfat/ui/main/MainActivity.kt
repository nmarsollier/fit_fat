package com.nmarsollier.fitfat.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.viewpager.widget.ViewPager
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.MainActivityBinding
import com.nmarsollier.fitfat.utils.closeKeyboard
import com.nmarsollier.fitfat.utils.observe

class MainActivity : AppCompatActivity() {
    private val binding: MainActivityBinding by lazy {
        MainActivityBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<MainViewModel>()

    private var lastState: MainState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.navigation.setOnNavigationItemSelectedListener { item ->
            viewModel.setCurrentSelectedTab(
                when (item.itemId) {
                    R.id.menu_options -> Screen.OPTIONS
                    R.id.menu_stats -> Screen.PROGRESS
                    else -> Screen.HOME
                }
            )
            true
        }

        binding.pager.adapter = MainPageAdapter(supportFragmentManager)
        binding.pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentSelectedTab(
                    when (position) {
                        0 -> Screen.OPTIONS
                        2 -> Screen.PROGRESS
                        else -> Screen.HOME
                    }
                )
            }
        })

        viewModel.state.observe(viewModel.viewModelScope) { state ->
            val currentTab = lastState?.selectedTab
            lastState = state
            if (currentTab != lastState?.selectedTab) {
                setCurrentSelectedTab()
            }
        }

        viewModel.load(this)
    }

    override fun onBackPressed() {
        if (lastState?.selectedTab != Screen.HOME) {
            viewModel.setCurrentSelectedTab(Screen.HOME)
        } else {
            super.onBackPressed()
        }
    }

    private fun setCurrentSelectedTab() {
        val currentScreen = lastState?.selectedTab ?: Screen.HOME

        closeKeyboard()

        binding.pager.currentItem = currentScreen.ordinal

        binding.navigation.selectedItemId = when (currentScreen) {
            Screen.OPTIONS -> R.id.menu_options
            Screen.PROGRESS -> R.id.menu_stats
            Screen.HOME -> R.id.menu_home
        }

        setTitle(
            when (currentScreen) {
                Screen.OPTIONS -> R.string.home_options_title
                Screen.PROGRESS -> R.string.home_progress_title
                Screen.HOME -> R.string.home_measure_title
            }
        )
    }
}
