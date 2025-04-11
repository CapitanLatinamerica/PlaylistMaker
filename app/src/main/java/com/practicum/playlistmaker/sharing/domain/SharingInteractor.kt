package com.practicum.playlistmaker.sharing.domain

import android.content.Context

interface SharingInteractor {
    fun shareApp(context: Context)
    fun openSupport(context: Context)
    fun openTerms(context: Context)
}