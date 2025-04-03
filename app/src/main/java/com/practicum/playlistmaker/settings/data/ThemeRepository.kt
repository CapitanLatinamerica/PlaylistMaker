package com.practicum.playlistmaker.settings.data

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun changeTheme(isDarkTheme: Boolean)
}
