package com.practicum.playlistmaker.settings.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.data.ThemeRepository

class SettingsInteractorImpl(
    private val themeRepository: ThemeRepository,
    private val context: Context // Передаем контекст сюда
) : SettingsInteractor {
    override fun isDarkThemeEnabled(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun changeTheme(isDark: Boolean) {
        themeRepository.changeTheme(isDark)
    }

    // Реализация методов шеринга
    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text))
        }
            context.startActivity(Intent.createChooser(shareIntent, null))
    }

    override fun openTerms() {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(context.getString(R.string.user_agreement_url))
            })
    }

    override fun openSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_text)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
        }
            context.startActivity(intent)
    }
}
