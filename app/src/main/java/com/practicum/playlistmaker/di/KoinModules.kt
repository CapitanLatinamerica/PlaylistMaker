package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.app.PREFERENCE_NAME
import com.practicum.playlistmaker.media.MediaViewModel
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
import androidx.room.Room
import com.practicum.playlistmaker.db.data.AppDatabase
import com.practicum.playlistmaker.db.data.repository.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.db.domain.FavoriteTracksInteractor
import com.practicum.playlistmaker.db.data.FavoriteTracksInteractorImpl
import com.practicum.playlistmaker.db.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.media.fragmentes.viewmodel.FavoriteTracksViewModel


val appModule = module {
        // Общие зависимости
        single<SharedPreferences> { provideSharedPreferences(androidContext()) }

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
        viewModel { SearchViewModel(get(), get()) }
    }

// Модуль для работы с Room
val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "playlistmaker.db"
        ).build()
    }
    single { get<AppDatabase>().favoriteTrackDao() }
}


// Модуль настроек
val settingsModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<SettingsInteractor> { SettingsInteractorImpl(get()) }

    single<SharingRepository> { SharingRepositoryImpl(get()) }
    single<SharingInteractor> { SharingInteractorImpl(get()) }

    viewModel {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get(),
            context = get()
        )
    }
}

// Модуль медиатеки
val mediaModule = module {
    // Медиатека - УПРОЩЕННАЯ ВЕРСИЯ
    viewModel { MediaViewModel() } // Без параметров

    // Фрагменты медиатеки с параметрами
    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    viewModel { FavoriteTracksViewModel(get()) }
    viewModel { PlaylistsViewModel(get()) }
}

fun provideSharedPreferences(context: Context): SharedPreferences {                                 //Создает экземпляр SharedPreferences
    return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
}
