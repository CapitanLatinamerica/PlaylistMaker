package com.practicum.playlistmaker.root

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val PREFERENCE_NAME = "user_preferences"
const val PREFERENCE_THEME_KEY = "is_dark_theme_enabled"

sealed class ThemeMode(val mode: Int) {
    data object Light : ThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
    data object Dark : ThemeMode(AppCompatDelegate.MODE_NIGHT_YES)

    companion object {
        fun fromBoolean(isDark: Boolean): ThemeMode {
            return if (isDark) Dark else Light
        }
    }
}

class App : Application() {
    companion object {
        lateinit var instance: App                                                                  //Синглтон
            private set
    }

    private lateinit var preferences: SharedPreferences
    private lateinit var currentTheme: ThemeMode

    override fun onCreate() {
        super.onCreate()
        instance = this // Инициализация синглтона

        // Инициализация SharedPreferences
        preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

        // Загрузка текущей темы
        val isDarkTheme = preferences.getBoolean(PREFERENCE_THEME_KEY, false)
        currentTheme = ThemeMode.fromBoolean(isDarkTheme)

        // Установка темы приложения
        AppCompatDelegate.setDefaultNightMode(currentTheme.mode)
    }

    fun switchTheme(isDarkTheme: Boolean) {
        val newTheme = ThemeMode.fromBoolean(isDarkTheme)
        if (currentTheme != newTheme) {
            currentTheme = newTheme
            AppCompatDelegate.setDefaultNightMode(newTheme.mode)
            saveThemePreference(isDarkTheme)
        }
    }

    private fun saveThemePreference(isDarkTheme: Boolean) {
        preferences.edit()
            .putBoolean(PREFERENCE_THEME_KEY, isDarkTheme)
            .apply()
    }

    fun isDarkThemeEnabled(): Boolean {
        return currentTheme is ThemeMode.Dark
    }
}