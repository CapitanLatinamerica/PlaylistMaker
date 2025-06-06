package com.practicum.playlistmaker.player.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.presentation.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.data.repository.LikeStorage
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository,
    private val likeStorage: LikeStorage,
    private val favoriteTracksViewModel: FavoriteTracksViewModel
) : ViewModel() {

    private val HARDCODED_DURATION_MS = 30_000

    // Состояние плеера: публичное (StateFlow) и внутреннее (MutableStateFlow)
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.IDLE)
    val playerState: StateFlow<PlayerState> = _playerState

    // Оставшееся время воспроизведения трека (в миллисекундах)
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    private val _isLiked = MutableStateFlow<Boolean>(false)
    var isLiked: StateFlow<Boolean> = _isLiked

    private var track: Track? = null

    // Job-короутина, обновляющая прогресс воспроизведения
    private var playbackJob: Job? = null

    init {
        // Устанавливаем callback: когда воспроизведение закончится, переходим в состояние FINISHED
        playerRepository.setOnCompletionListener {
            stopPlayback() // Освобождаем ресурсы
            _playerState.value = PlayerState.FINISHED // Состояние — завершено
        }
    }

    fun setInitialDuration() {
        _progress.value = HARDCODED_DURATION_MS         // Устанавливаем начальный прогресс
    }

    // Запуск воспроизведения трека
    fun playTrack(track: Track) {
        if (_playerState.value == PlayerState.PLAYING) return

        _playerState.value = PlayerState.PREPARING

        val url = track.previewUrl
        if (url.isNullOrEmpty()) {
            _playerState.value = PlayerState.ERROR("Track preview URL is missing.")
            return
        }

        // Подготавливаем плеер и запускаем по готовности
        playerRepository.preparePlayer(
            url = url,
            onPrepared = {
                _playerState.value = PlayerState.PLAYING // Состояние — воспроизведение
                _progress.value = HARDCODED_DURATION_MS  // Сброс прогресса до начала
                startProgressUpdater()                   // Запускаем обновление прогресса
            },
            onError = { e ->
                _playerState.value = PlayerState.ERROR(e.message ?: "Unknown error")
            }
        )
    }

    // Обработка нажатия кнопки "Пауза / Воспроизвести"
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

    // Запускает корутину, которая обновляет прогресс каждые ХХ секунды
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

    // Останавливает обновление прогресса
    private fun stopProgressUpdater() {
        playbackJob?.cancel()
        playbackJob = null
    }

    // Остановка воспроизведения и сброс состояния
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

    //Обраюотаем нажатие кнопки лайка
    fun onLikeClicked() {
        val currentTrack = track ?: return                                                          // Получаем текущий трек
        viewModelScope.launch {
            val newState = likeStorage.toggleLike(currentTrack.trackId)                                 // Переключаем состояние лайка (добавление/удаление из избранного)
            _isLiked.value = newState                                                                   // Обновляем состояние лайка
            currentTrack.isFavorite = newState                                                                // Обновляем состояние трека
            val currentTime = System.currentTimeMillis() // Получаем текущее время
            Log.d("PlayerViewModel", "Track ${currentTrack.trackName} like state changed to: $newState, addedAt: $currentTime")

            if (newState) {
                // Добавляем трек в избранное, если он добавлен в избранное
                favoriteTracksViewModel.addTrackToFavorites(currentTrack)
            } else {
                // Удаляем трек из избранного, если он удален из избранного
                favoriteTracksViewModel.removeTrackFromFavorites(currentTrack)
            }
        }
    }

    fun setTrack(track: Track?) {
        if (track != null) {
            this.track = track
            val isTrackLiked = likeStorage.isLiked(track.trackId)
            _isLiked.value = isTrackLiked
        } else {
        }
    }
}
