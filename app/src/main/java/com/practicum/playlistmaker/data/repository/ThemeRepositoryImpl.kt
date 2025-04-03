package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.domain.repository.ThemeRepository
import com.practicum.playlistmaker.app.App

class ThemeRepositoryImpl : ThemeRepository {
    override fun setTheme(isDarkTheme: Boolean) {
        val app = App.instance
        app.switchTheme(isDarkTheme)
    }
}
