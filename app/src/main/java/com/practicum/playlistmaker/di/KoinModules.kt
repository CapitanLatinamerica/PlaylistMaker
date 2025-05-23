package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.app.PREFERENCE_NAME
import com.practicum.playlistmaker.media.MediaViewModel
import com.practicum.playlistmaker.media.fragmentes.viewmodel.FavoriteTracksViewModel
import com.practicum.playlistmaker.media.fragmentes.viewmodel.PlaylistsViewModel
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.domain.SearchInteractorImpl
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.SearchInteractor
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.SearchRepository
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.practicum.playlistmaker.settings.data.ThemeRepository
import com.practicum.playlistmaker.settings.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.sharing.data.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.data.SharingRepository
import com.practicum.playlistmaker.sharing.data.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
        // Общие зависимости
        single<SharedPreferences> { provideSharedPreferences(androidContext()) }

        // Медиатека - УПРОЩЕННАЯ ВЕРСИЯ
        viewModel { MediaViewModel() } // Без параметров

        // Фрагменты медиатеки с параметрами
        viewModel { (fragment: Fragment) ->
            FavoriteTracksViewModel(fragment)
        }
        viewModel { (fragment: Fragment) ->
            PlaylistsViewModel(fragment)
        }

        // Настройки темы
        single<ThemeRepository> { ThemeRepositoryImpl(get()) }
        single<SettingsInteractor> { SettingsInteractorImpl(get()) }

        // Sharing (кнопки)
        single<SharingRepository> { SharingRepositoryImpl(get()) }                                  // Репозиторий
        single<SharingInteractor> { SharingInteractorImpl(get()) }                                  // Интерактор

        // Поиск и история
        single<SearchRepository> { SearchRepositoryImpl(get()) }
        single<SearchHistoryRepository> {
            SearchHistoryRepositoryImpl(
                get()
            )
        }
        single<SearchInteractor> { SearchInteractorImpl(get()) }
        single<SearchHistoryInteractor> { SearchHistoryInteractor(get()) }
        single { ITunesService.create() } // Retrofit сервис

        // ViewModel для Settings
        viewModel {
            SettingsViewModel(
                sharingInteractor = get(),
                settingsInteractor = get(),
                context = get()
            )
        }

        viewModel { SearchViewModel(get(), get()) }
    }

fun provideSharedPreferences(context: Context): SharedPreferences {                                 //Создает экземпляр SharedPreferences
    return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
}
