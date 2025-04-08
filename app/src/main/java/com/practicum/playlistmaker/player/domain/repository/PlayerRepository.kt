package com.practicum.playlistmaker.player.domain.repository

interface PlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onError: (Exception) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPosition(): Int
    fun releasePlayer()
    fun seekTo(position: Int)
    fun setOnCompletionListener(listener: () -> Unit)
}