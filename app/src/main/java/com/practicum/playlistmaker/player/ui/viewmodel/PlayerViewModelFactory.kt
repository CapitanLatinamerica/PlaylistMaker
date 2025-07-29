package com.practicum.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.db.domain.playlists.PlaylistInteractor
import com.practicum.playlistmaker.db.presentation.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.data.repository.LikeStorage
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor

class PlayerViewModelFactory(
    private val playerRepository: PlayerRepository,
    private val playlistInteractor: PlaylistInteractor,
    private val likeStorage: LikeStorage,
    private val favoriteTracksViewModel : FavoriteTracksViewModel,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(playerRepository, playlistInteractor, likeStorage, favoriteTracksViewModel, searchHistoryInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

