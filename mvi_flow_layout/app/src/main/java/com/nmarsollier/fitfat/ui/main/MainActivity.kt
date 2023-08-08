package com.nmarsollier.fitfat.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.databinding.MainActivityBinding
import com.nmarsollier.fitfat.model.firebase.FirebaseRepository
import com.nmarsollier.fitfat.useCases.FirebaseUseCase
import com.nmarsollier.fitfat.utils.closeKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        MainActivityBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<MainViewModel>()

    private var lastState: MainState? = null

    @Inject
    lateinit var firebaseRepository: FirebaseRepository

    @Inject
    lateinit var firebaseUseCase: FirebaseUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainScope().launch(Dispatchers.IO) {
            firebaseRepository.checkAlreadyLoggedIn(firebaseUseCase)
        }

        setContentView(binding.root)

        binding.navigation.setOnItemSelectedListener { item ->
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

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                val currentTab = lastState?.selectedTab
                lastState = state
                if (currentTab != lastState?.selectedTab) {
                    setCurrentSelectedTab()
                }
            }
        }
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
