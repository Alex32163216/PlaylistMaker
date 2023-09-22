package com.example.playlistmaker.presentation.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate

class Application : Application() {

    companion object {
        const val APPLICATION = "APPLICATION"
        const val DARK_THEME = "DARK_THEME"
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(APPLICATION, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME, isDarkThemeOn())
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        if (darkThemeEnabled != darkTheme) {
            darkTheme = darkThemeEnabled
            val sharedPrefs = getSharedPreferences(APPLICATION, MODE_PRIVATE)
            sharedPrefs.edit()
                .putBoolean(DARK_THEME, darkTheme)
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