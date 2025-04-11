package com.practicum.playlistmaker.sharing.data

import android.content.Context
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(private val sharingRepository: SharingRepository) : SharingInteractor {
    override fun shareApp(context: Context) {
        sharingRepository.shareApp(context)
    }

    override fun openSupport(context: Context) {
        sharingRepository.openSupport(context)
    }

    override fun openTerms(context: Context) {
        sharingRepository.openTerms(context)
    }
}