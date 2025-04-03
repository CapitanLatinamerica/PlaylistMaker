package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences

class ThemeRepositoryImpl(private val sharedPreferences: SharedPreferences) : ThemeRepository {

    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    override fun changeTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, isDark).apply()
    }
}
