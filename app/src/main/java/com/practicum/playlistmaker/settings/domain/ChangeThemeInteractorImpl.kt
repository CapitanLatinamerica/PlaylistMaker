package com.practicum.playlistmaker.settings.domain

class ChangeThemeInteractorImpl(
    private val themeRepository: com.practicum.playlistmaker.domain.repository.ThemeRepository
) : ChangeThemeInteractor {

    override fun setTheme(isDarkTheme: Boolean) {
        themeRepository.setTheme(isDarkTheme)
    }
}
