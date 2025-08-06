package com.practicum.playlistmaker.navigation

interface NavigationGuard {
    fun shouldBlockNavigation(): Boolean
    fun requestExitConfirmation(onConfirm: () -> Unit)
}