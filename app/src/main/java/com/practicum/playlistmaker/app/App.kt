package com.practicum.playlistmaker.app

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.component.get

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

class App : Application(), KoinComponent {

    // Убираем lateinit, переменная будет инициализироваться сразу
    private var currentTheme: ThemeMode = ThemeMode.Light

    override fun onCreate() {
        super.onCreate()

        // Стартуем Koin
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        // Получаем SharedPreferences через Koin
        val preferences: SharedPreferences = get()
        val isDarkTheme = preferences.getBoolean(PREFERENCE_THEME_KEY, false)

        // Инициализируем тему
        currentTheme = ThemeMode.fromBoolean(isDarkTheme)

        // Устанавливаем тему
        AppCompatDelegate.setDefaultNightMode(currentTheme.mode)
    }

    // Метод для переключения темы
    fun switchTheme(isDarkTheme: Boolean) {
        val preferences: SharedPreferences = get()
        preferences.edit().putBoolean(PREFERENCE_THEME_KEY, isDarkTheme).apply()

        // Обновляем тему
        currentTheme = ThemeMode.fromBoolean(isDarkTheme)
        AppCompatDelegate.setDefaultNightMode(currentTheme.mode)
    }
}
