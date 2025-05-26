package com.practicum.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.data.repository.LikeStorage
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository

class PlayerViewModelFactory(
    private val playerRepository: PlayerRepository,
    private val likeStorage: LikeStorage
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(playerRepository, likeStorage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

