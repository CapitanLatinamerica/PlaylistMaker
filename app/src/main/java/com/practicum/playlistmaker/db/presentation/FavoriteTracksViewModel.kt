package com.practicum.playlistmaker.db.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.FavoriteTracksInteractor
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    // Храним избранные треки
    val favoriteTracks = favoriteTracksInteractor.getAllFavoriteTracks()

    // Добавление трека в избранное
    fun addTrackToFavorites(track: Track) {
        viewModelScope.launch {
            favoriteTracksInteractor.addTrackToFavorites(track)
        }
    }

    // Удаление трека из избранного
    fun removeTrackFromFavorites(track: Track) {
        viewModelScope.launch {
            favoriteTracksInteractor.removeTrackFromFavorites(track)
        }
    }
}
