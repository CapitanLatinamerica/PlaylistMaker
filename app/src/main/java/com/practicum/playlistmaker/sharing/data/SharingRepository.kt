package com.practicum.playlistmaker.sharing.data

import android.content.Context

interface SharingRepository {
    fun shareApp(context: Context)
    fun openTerms(context: Context)
    fun openSupport(context: Context)
}