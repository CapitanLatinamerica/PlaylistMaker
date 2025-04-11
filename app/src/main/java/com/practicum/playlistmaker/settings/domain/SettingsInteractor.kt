package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun changeTheme(isDark: Boolean)
}