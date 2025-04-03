package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R

class SharingRepositoryImpl(private val context: Context) : SharingRepository {

    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text))
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_app)))
    }

    override fun openTerms() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.user_agreement_url)))
        context.startActivity(browserIntent)
    }

    override fun openSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_manager)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_text))
        }
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.btn_help)))
    }
}
