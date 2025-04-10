package com.practicum.playlistmaker.player.ui.viewmodel

import android.os.Handler
import android.os.Looper
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

    private val HARDCODED_DURATION_MS = 30_000

    // Состояние плеера
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.IDLE)
    val playerState: StateFlow<PlayerState> = _playerState

    // Прогресс трека (в миллисекундах)
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    fun setInitialDuration() {
        _progress.value = HARDCODED_DURATION_MS         // Устанавливаем начальный прогресс
    }

    private var playbackJob: Job? = null

    private var handler = Handler(Looper.getMainLooper())
    private val updateProgress = object : Runnable {
        override fun run() {
            _progress.value = playerRepository.getCurrentPosition()
            handler.postDelayed(this, 1000) // Обновляем каждую секунду
        }
    }

    init {
        playerRepository.setOnCompletionListener {
            _playerState.value = PlayerState.IDLE
            _progress.value = HARDCODED_DURATION_MS
            playbackJob?.cancel()
            handler.removeCallbacks(updateProgress)
        }
    }

    fun playTrack(track: Track) {
        if (_playerState.value == PlayerState.PLAYING) return

        _playerState.value = PlayerState.PREPARING

        track.previewUrl?.let { url ->                                                              // Передаём проверку на null для previewUrl
            playerRepository.preparePlayer(
                url = url,
                onPrepared = {
                    _playerState.value = PlayerState.PLAYING
                    _progress.value = HARDCODED_DURATION_MS
                    startPlaybackCoroutine()
                },
                // Обработка ошибки
                onError = { e ->
                    _playerState.value = PlayerState.ERROR(e.message ?: "Unknown error")
                }
            )
        } ?: run {
            _playerState.value = PlayerState.ERROR("Track preview URL is missing.")
        }
    }

    // Управление воспроизведением
    fun togglePlayPause() {
        when (_playerState.value) {
            PlayerState.PLAYING -> {
                playerRepository.pausePlayer()
                _playerState.value = PlayerState.PAUSED
                playbackJob?.cancel()
            }
            PlayerState.PAUSED -> {
                playerRepository.startPlayer()
                _playerState.value = PlayerState.PLAYING
                startPlaybackCoroutine()
            }
            else -> {}
        }
    }

    override fun onCleared() {
        handler.removeCallbacks(updateProgress)
        playerRepository.releasePlayer()
        super.onCleared()
        stopPlayback()
    }

    fun stopPlayback() {
        playerRepository.releasePlayer()
        _playerState.value = PlayerState.IDLE
        _progress.value = 0
        playbackJob?.cancel()
    }

    private fun startPlaybackCoroutine() {
        playbackJob = viewModelScope.launch {
            while (_playerState.value == PlayerState.PLAYING) {
                val currentPosition = playerRepository.getCurrentPosition()
                val remainingTime = (HARDCODED_DURATION_MS - currentPosition).coerceAtLeast(0) // Чтобы не ушёл в минус
                _progress.value = remainingTime
                delay(500L)
            }
        }
    }

    sealed class PlayerState {                                                                      // Состояния плеера
        object IDLE : PlayerState()
        object PREPARING : PlayerState()
        object PLAYING : PlayerState()
        object PAUSED : PlayerState()
        data class ERROR(val message: String) : PlayerState()
    }
}
