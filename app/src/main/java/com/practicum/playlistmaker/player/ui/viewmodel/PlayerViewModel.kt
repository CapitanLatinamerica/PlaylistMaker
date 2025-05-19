package com.practicum.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    sealed class PlayerState {
        object IDLE : PlayerState()
        object PREPARING : PlayerState()
        object PLAYING : PlayerState()
        object PAUSED : PlayerState()
        object FINISHED : PlayerState()
        data class ERROR(val message: String) : PlayerState()
    }

    private val HARDCODED_DURATION_MS = 30_000

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.IDLE)
    val playerState: StateFlow<PlayerState> = _playerState

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    private var playbackJob: Job? = null

    init {
        playerRepository.setOnCompletionListener {
            stopPlayback()
            _playerState.value = PlayerState.FINISHED
        }
    }

    fun setInitialDuration() {
        _progress.value = HARDCODED_DURATION_MS         // Устанавливаем начальный прогресс
    }

    fun playTrack(track: Track) {
        if (_playerState.value == PlayerState.PLAYING) return

        _playerState.value = PlayerState.PREPARING

        val url = track.previewUrl
        if (url.isNullOrEmpty()) {
            _playerState.value = PlayerState.ERROR("Track preview URL is missing.")
            return
        }

        playerRepository.preparePlayer(
            url = url,
            onPrepared = {
                _playerState.value = PlayerState.PLAYING
                _progress.value = HARDCODED_DURATION_MS
                startProgressUpdater()
            },
            onError = { e ->
                _playerState.value = PlayerState.ERROR(e.message ?: "Unknown error")
            }
        )
    }

    fun togglePlayPause() {
        when (_playerState.value) {
            PlayerState.PLAYING -> {
                playerRepository.pausePlayer()
                _playerState.value = PlayerState.PAUSED
                stopProgressUpdater()
            }
            PlayerState.PAUSED -> {
                playerRepository.startPlayer()
                _playerState.value = PlayerState.PLAYING
                startProgressUpdater()
            }
            else -> {}
        }
    }

    private fun startProgressUpdater() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_playerState.value == PlayerState.PLAYING) {
                val currentPosition = playerRepository.getCurrentPosition()
                val remaining = (HARDCODED_DURATION_MS - currentPosition).coerceAtLeast(0)
                _progress.value = remaining
                delay(300L)
            }
        }
    }

    private fun stopProgressUpdater() {
        playbackJob?.cancel()
        playbackJob = null
    }

    fun stopPlayback() {
        playerRepository.releasePlayer()
        _playerState.value = PlayerState.IDLE
        _progress.value = 0
        stopProgressUpdater()
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
    }
}
