package com.practicum.playlistmaker.creator                                                                 // Пакет для создания зависимостей

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.search.data.network.ITunesService                                               // Импортируем ITunesService
import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.data.repository.TrackRepositoryImpl                              // Импортируем TrackRepositoryImpl
import com.practicum.playlistmaker.settings.data.ThemeRepository
import com.practicum.playlistmaker.settings.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.domain.interactors.ChangeThemeInteractor
import com.practicum.playlistmaker.domain.interactors.ChangeThemeInteractorImpl
import com.practicum.playlistmaker.domain.interactors.GetSearchHistoryInteractor
import com.practicum.playlistmaker.domain.interactors.GetSearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactors.SaveSearchHistoryInteractor
import com.practicum.playlistmaker.domain.interactors.SaveSearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactors.SearchTracksInteractor
import com.practicum.playlistmaker.domain.interactors.SearchTracksInteractorImpl
import com.practicum.playlistmaker.domain.repository.TrackRepository                                // Импортируем TrackRepository
import com.practicum.playlistmaker.app.App
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModelFactory
import com.practicum.playlistmaker.sharing.data.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.data.SharingRepository
import com.practicum.playlistmaker.sharing.data.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

object Creator {

    // Метод для создания репозитория треков
    fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(
            iTunesService = provideITunesService(),                                                 // Переиспользуем сервис
            searchHistory = provideSearchHistory()                                                  // Переиспользуем SearchHistory
        )
    }

    //Метод для создания интерактора поиска треков
    fun provideSearchTracksInteractor(): SearchTracksInteractor {
        val trackRepository = provideTrackRepository()
        return SearchTracksInteractorImpl(trackRepository)
    }

    //Метод для создания интерактора сохранения истории поиска
    fun provideSaveSearchHistoryInteractor(): SaveSearchHistoryInteractor {
        val searchHistory = provideSearchHistory()                                                  // Создаём экземпляр SearchHistory
        return SaveSearchHistoryInteractorImpl(searchHistory)                                       // Передаём SearchHistory
    }

    //Метод для создания интерактора получения истории поиска
    fun provideGetSearchHistoryInteractor(): GetSearchHistoryInteractor {
        val trackRepository = provideTrackRepository()
        return GetSearchHistoryInteractorImpl(trackRepository)
    }

    //Метод для создания сервиса iTunes
    private fun provideITunesService(): ITunesService {
        return ITunesService.create()
    }

    //Метод для создания истории поиска
    private fun provideSearchHistory(): SearchHistory {
        return SearchHistory(provideSharedPreferences())                                            // Создаем экземпляр SearchHistory
    }

    //Метод для создания SharedPreferences
    private fun provideSharedPreferences(): SharedPreferences {
        return App.instance.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
    }
    // Метод для создания интерактора изменения темы
    fun provideChangeThemeInteractor(): ChangeThemeInteractor {
        val themeRepository = provideThemeRepository()
        return ChangeThemeInteractorImpl(themeRepository)
    }

    // Метод для создания репозитория для темы
    fun provideThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(provideSharedPreferences())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideThemeRepository())
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(provideSharingRepository(context))
    }

    fun provideSharingRepository(context: Context): SharingRepository {
        return SharingRepositoryImpl(context)
    }

    fun provideSettingsViewModelFactory(context: Context): ViewModelProvider.Factory {
        return SettingsViewModelFactory(
            provideSharingInteractor(context),
            provideSettingsInteractor()
        )
    }
}
