package com.practicum.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onError: (Exception) -> Unit) {
        try {
            mediaPlayer.reset() // Сброс перед повторным использованием
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepare() // Синхронная подготовка
            mediaPlayer.start()
            onPrepared()
        } catch (e: Exception) {
            onError(e)
        }
    }

    override fun startPlayer() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener { listener() }
    }

    override fun getDuration(): Int = mediaPlayer.duration
}