package com.practicum.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private val _progressFlow = MutableSharedFlow<Int>(replay = 1)

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

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun observeProgress(): Flow<Int> = _progressFlow

    private fun startProgressTracking() {
        if (progressJob?.isActive == true) return
        progressJob = scope.launch {
            while (isPlaying()) {
                _progressFlow.emit(getCurrentPosition())
                delay(300L)
            }
        }
    }

    override fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }
}