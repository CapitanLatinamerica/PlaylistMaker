package com.practicum.playlistmaker.settings.ui.viewmodel;

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.app.App
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
        private val sharingInteractor: SharingInteractor,
        private val settingsInteractor: SettingsInteractor,
        private val context: Context // Добавьте контекст
) : ViewModel() {

        private val _isDarkTheme = MutableLiveData<Boolean>()
        val isDarkTheme: LiveData<Boolean> = _isDarkTheme

        init {
                _isDarkTheme.value = settingsInteractor.isDarkThemeEnabled()
        }

        fun toggleTheme(isDark: Boolean) {
                settingsInteractor.changeTheme(isDark)  // Меняем тему через интерактор
                _isDarkTheme.value = isDark  // Обновляем LiveData для UI
                (context.applicationContext as App).switchTheme(isDark) // Применяем тему в Application
        }

        fun shareApp(context: Context) {
                sharingInteractor.shareApp(context)
        }

        fun contactSupport(context: Context) {
                sharingInteractor.openSupport(context)
        }

        fun openTerms(context: Context) {
                sharingInteractor.openTerms(context)
        }
}
