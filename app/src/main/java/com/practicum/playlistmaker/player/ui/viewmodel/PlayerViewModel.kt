package com.practicum.playlistmaker.player.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    // Состояние плеера
    private val _playerState = MutableLiveData<PlayerState>(PlayerState.IDLE)
    val playerState: LiveData<PlayerState> = _playerState

    // Прогресс трека (в миллисекундах)
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    // Текущий трек
    private val _currentTrack = MutableLiveData<Track?>()
    val currentTrack: LiveData<Track?> = _currentTrack

    private var handler = Handler(Looper.getMainLooper())
    private val updateProgress = object : Runnable {
        override fun run() {
            _progress.value = playerRepository.getCurrentPosition()
            handler.postDelayed(this, 1000) // Обновляем каждую секунду
        }
    }

    init {
        Log.e("LIFECYCLE", "ViewModel initialized")
        check(playerRepository != null) { "PlayerRepository is null!" }

        playerRepository.setOnCompletionListener {
            _playerState.value = PlayerState.IDLE
            handler.removeCallbacks(updateProgress)
        }
    }

    fun playTrack(track: Track) {

        track.previewUrl?.let { url ->
            _playerState.value = PlayerState.PREPARING
            playerRepository.preparePlayer(
                url = url,
                onPrepared = {
                    _playerState.value = PlayerState.PLAYING
                    startProgressUpdates()
                },
                onError = { e ->
                    Log.e("PLAYER_DEBUG", "Playback error: ${e.message}")
                    _playerState.value = PlayerState.ERROR(e.message ?: "Unknown error")
                }
            )
        } ?: Log.e("PLAYER_DEBUG", "playTrack(): previewUrl is null!")
    }

    // Управление воспроизведением
    fun togglePlayPause() {
        when (_playerState.value) {
            PlayerState.PLAYING -> {
                playerRepository.pausePlayer()
                _playerState.value = PlayerState.PAUSED
                handler.removeCallbacks(updateProgress)
            }
            PlayerState.PAUSED -> {
                playerRepository.startPlayer()
                _playerState.value = PlayerState.PLAYING
                startProgressUpdates()
            }
            else -> {} // Игнорируем другие состояния
        }
    }

    private fun startProgressUpdates() {
        handler.post(updateProgress)
    }

    override fun onCleared() {
        handler.removeCallbacks(updateProgress)
        playerRepository.releasePlayer()
        super.onCleared()
    }

    // Состояния плеера
    sealed class PlayerState {
        object IDLE : PlayerState()
        object PREPARING : PlayerState()
        object PLAYING : PlayerState()
        object PAUSED : PlayerState()
        data class ERROR(val message: String) : PlayerState()
    }
}

/**/