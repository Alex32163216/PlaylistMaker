package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate

class Additionally : Application() {

    companion object {
        const val ADDITIONALLY = "ADDITIONALLY"
        const val DARKWING_DUCK = "DARKWING_DUCK"
    }

    var darkwingDuck = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(ADDITIONALLY, MODE_PRIVATE)
        darkwingDuck = sharedPrefs.getBoolean(DARKWING_DUCK, isDarkThemeOn())
        switchTheme(darkwingDuck)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        if (darkThemeEnabled != darkwingDuck) {
            darkwingDuck = darkThemeEnabled
            val sharedPrefs = getSharedPreferences(ADDITIONALLY, MODE_PRIVATE)
            sharedPrefs.edit()
                .putBoolean(DARKWING_DUCK, darkwingDuck)
                .apply()
        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun Context.isDarkThemeOn() = resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}