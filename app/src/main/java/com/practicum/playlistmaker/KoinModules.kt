package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.app.PREFERENCE_NAME
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

        // Настройки темы
        single<ThemeRepository> { ThemeRepositoryImpl(get()) }
        single<SettingsInteractor> { SettingsInteractorImpl(get()) }

        // Sharing (кнопки)
        single<SharingRepository> { SharingRepositoryImpl(get()) } // Контекст берётся из Koin
        single<SharingInteractor> { SharingInteractorImpl(get()) }

        // ViewModel для Settings
        viewModel {
            SettingsViewModel(
                sharingInteractor = get(),
                settingsInteractor = get(),
                context = get()
            )
        }
    }

fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
}
