package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R

class SharingRepositoryImpl(private val context: Context) : SharingRepository {

    override fun shareApp(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text))
        }
        context.startActivity(                                                                      // Открываем системный диалог выбора приложения для отправки
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_app)
            )
        )
    }

    override fun openSupport(context: Context) {
            val uri = Uri.parse("mailto:${context.getString(R.string.support_manager)}")    // Формируем mailto URI с email поддержки
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {                                   // Создаем intent для отправки email
                putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_manager)))
                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_text))
            }
                context.startActivity(intent)
    }

    override fun openTerms(context: Context) {
            val browserIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.user_agreement_url)))
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
    }
}
