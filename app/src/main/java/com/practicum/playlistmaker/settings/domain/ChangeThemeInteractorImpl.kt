package com.practicum.playlistmaker.settings.domain

class ChangeThemeInteractorImpl(
    private val themeRepository: com.practicum.playlistmaker.settings.data.ThemeRepository
) : ChangeThemeInteractor {

    override fun setTheme(isDarkTheme: Boolean) {
        themeRepository.changeTheme(isDarkTheme)
    }
}
