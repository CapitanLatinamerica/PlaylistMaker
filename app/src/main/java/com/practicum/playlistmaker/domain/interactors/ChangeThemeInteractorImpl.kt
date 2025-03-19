package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.repository.ThemeRepository

class ChangeThemeInteractorImpl(
    private val themeRepository: ThemeRepository
) : ChangeThemeInteractor {

    override fun changeTheme(isDarkTheme: Boolean) {
        themeRepository.setTheme(isDarkTheme)
    }
}
