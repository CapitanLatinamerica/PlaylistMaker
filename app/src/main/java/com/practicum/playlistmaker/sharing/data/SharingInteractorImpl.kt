package com.practicum.playlistmaker.sharing.data

import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(private val sharingRepository: SharingRepository) : SharingInteractor {
    override fun shareApp() {
        sharingRepository.shareApp()
    }

    override fun openTerms() {
        sharingRepository.openTerms()
    }

    override fun openSupport() {
        sharingRepository.openSupport()
    }
}