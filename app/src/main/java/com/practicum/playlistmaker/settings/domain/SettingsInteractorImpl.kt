package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.data.ThemeRepository

class SettingsInteractorImpl(
    private val themeRepository: ThemeRepository,
) : SettingsInteractor {
    override fun isDarkThemeEnabled(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun changeTheme(isDark: Boolean) {
        themeRepository.changeTheme(isDark)
    }
}
