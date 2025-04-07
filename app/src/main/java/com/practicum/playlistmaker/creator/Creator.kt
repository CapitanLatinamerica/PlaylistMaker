package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.app.App
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.*
import com.practicum.playlistmaker.search.domain.GetSearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.GetSearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.SaveSearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.SaveSearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.SearchHistory
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.SearchInteractorImpl
import com.practicum.playlistmaker.search.domain.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.SearchTracksInteractorImpl
import com.practicum.playlistmaker.search.domain.TrackRepository
import com.practicum.playlistmaker.settings.data.ThemeRepository
import com.practicum.playlistmaker.settings.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.settings.domain.ChangeThemeInteractor
import com.practicum.playlistmaker.settings.domain.ChangeThemeInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModelFactory
import com.practicum.playlistmaker.sharing.data.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.data.SharingRepository
import com.practicum.playlistmaker.sharing.data.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

object Creator {

    // Создаем репозиторий для треков
    fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(
            iTunesService = provideITunesService(),
            searchHistory = provideSearchHistory()  // Reusing SearchHistory instance
        )
    }

    // Создаем интерактор для поиска треков
    fun provideSearchTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(provideTrackRepository())
    }

    // Создаем интерактор для сохранения истории поиска
    fun provideSaveSearchHistoryInteractor(): SaveSearchHistoryInteractor {
        return SaveSearchHistoryInteractorImpl(provideSearchHistory())
    }

    // Создаем интерактор для получения истории поиска
    fun provideGetSearchHistoryInteractor(): GetSearchHistoryInteractor {
        return GetSearchHistoryInteractorImpl(provideTrackRepository())
    }

    // Создаем сервис для iTunes
    private fun provideITunesService(): ITunesService {
        return ITunesService.create()
    }

    // Создаем историю поиска
    private fun provideSearchHistory(): SearchHistory {
        return SearchHistory(provideSharedPreferences()) // Создаем с SharedPreferences
    }

    // Создаем SharedPreferences
    private fun provideSharedPreferences(): SharedPreferences {
        return App.instance.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
    }

 /*   // Создаем интерактор для изменения темы
    fun provideChangeThemeInteractor(): ChangeThemeInteractor {
        return ChangeThemeInteractorImpl(provideThemeRepository())
    }*/

    // Создаем репозиторий для темы
    fun provideThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(provideSharedPreferences())
    }

    // Создаем интерактор для настроек
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideThemeRepository())
    }

    // Создаем интерактор для обмена
    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(provideSharingRepository(context))
    }

    // Репозиторий для обмена
    fun provideSharingRepository(context: Context): SharingRepository {
        return SharingRepositoryImpl(context)
    }

    // Фабрика для создания ViewModel для настроек
    fun provideSettingsViewModelFactory(context: Context): ViewModelProvider.Factory {
        return SettingsViewModelFactory(
            provideSharingInteractor(context),
            provideSettingsInteractor()
        )
    }

    // Создаем репозиторий для истории поиска
    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPreferences)
    }

    // Создаем интерактор для истории поиска
    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractor(provideSearchHistoryRepository(context))
    }

    // Создаем интерактор для поиска
    fun provideSearchInteractor(context: Context): SearchInteractor {
        val iTunesService = ITunesService.create()
        val searchRepository: SearchRepository = SearchRepositoryImpl(iTunesService)
        return SearchInteractorImpl(searchRepository)
    }
}
