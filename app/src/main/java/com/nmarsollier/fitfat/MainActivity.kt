package com.nmarsollier.fitfat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.crashlytics.android.Crashlytics
import com.nmarsollier.fitfat.model.getRoomDatabase
import com.nmarsollier.fitfat.utils.closeKeyboard
import com.nmarsollier.fitfat.utils.runInBackground
import com.nmarsollier.fitfat.utils.runInForeground
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {
    private var currentScreen = Screen.HOME

    enum class Screen {
        OPTIONS, HOME, PROGRESS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())

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

        loadSettings()
    }

    override fun onResume() {
        super.onResume()
        setCurrentSelectedTab(forceRefresh = true)
    }

    override fun onBackPressed() {
        if (currentScreen != Screen.HOME) {
            setCurrentSelectedTab(Screen.HOME)
        } else {
            super.onBackPressed()
        }
    }

    private fun loadSettings() {
        val context = applicationContext ?: return

        runInBackground {
            val userSettings = getRoomDatabase(context).userDao().getUserSettings()
            runInForeground {
                if (userSettings.isNew()) {
                    setCurrentSelectedTab(Screen.OPTIONS)
                    Toast.makeText(context, R.string.main_setup_app_text, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setCurrentSelectedTab(selectedScreen: Screen = currentScreen, forceRefresh: Boolean = false) {
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
