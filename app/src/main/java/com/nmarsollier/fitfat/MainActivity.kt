package com.nmarsollier.fitfat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.nmarsollier.fitfat.utils.closeKeyboard
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {
    private var currentScreen = Screen.HOME

    enum class Screen {
        OPTIONS, HOME, PROGRESS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        vNavigation.setOnNavigationItemSelectedListener { item ->
            setCurrentSelectedTab(
                when (item.itemId) {
                    R.id.menu_options -> Screen.OPTIONS
                    R.id.menu_stats -> Screen.PROGRESS
                    else -> Screen.HOME
                }
            )
            true
        }

        vPager.adapter = Adapter(supportFragmentManager)
        vPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                setCurrentSelectedTab(
                    when (position) {
                        0 -> Screen.OPTIONS
                        2 -> Screen.PROGRESS
                        else -> Screen.HOME
                    }
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setCurrentSelectedTab(currentScreen, true)
    }

    override fun onBackPressed() {
        if (currentScreen != Screen.HOME) {
            setCurrentSelectedTab(Screen.HOME)
        } else {
            super.onBackPressed()
        }
    }

    private fun setCurrentSelectedTab(selectedScreen: Screen, forceRefresh: Boolean = false) {
        if (currentScreen != selectedScreen || forceRefresh) {
            currentScreen = selectedScreen

            closeKeyboard()

            vPager.currentItem = currentScreen.ordinal

            vNavigation.selectedItemId = when (currentScreen) {
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
}

private class Adapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount() = MainActivity.Screen.values().size

    override fun getItem(position: Int): Fragment {
        return when (MainActivity.Screen.values()[position]) {
            MainActivity.Screen.PROGRESS -> MainStats.newInstance()
            MainActivity.Screen.OPTIONS -> MainOptions.newInstance()
            MainActivity.Screen.HOME -> MainHome.newInstance()
        }
    }
}
