package com.practicum.playlistmaker.domain.interactors

class ChangeThemeInteractorImpl(
    private val themeRepository: com.practicum.playlistmaker.settings.data.ThemeRepository
) : ChangeThemeInteractor {

    override fun changeTheme(isDarkTheme: Boolean) {
        themeRepository.changeTheme(isDarkTheme)
    }
}
