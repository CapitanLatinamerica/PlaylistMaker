package com.practicum.playlistmaker.player.domain.model

data class AudioPlayerUiState(
    val playerState: PlayerState = PlayerState.IDLE,
    val isFavorite: Boolean = false
)
