package com.practicum.playlistmaker.db.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.FavoriteTracksInteractor
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    // Получаем StateFlow
    private val _favoriteTracks = MutableStateFlow<List<Track>>(emptyList())
    val favoriteTracks: StateFlow<List<Track>> = _favoriteTracks

    init {
        observeFavoriteTracks()
    }

    // Метод для добавления трека в избранное
    fun addTrackToFavorites(track: Track) {
        viewModelScope.launch {
            favoriteTracksInteractor.addTrackToFavorites(track)
        }
    }

    // Метод для удаления трека из избранного
    fun removeTrackFromFavorites(track: Track) {
        viewModelScope.launch {
            favoriteTracksInteractor.removeTrackFromFavorites(track)
        }
    }

    private fun observeFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor.getAllFavoriteTracks().collectLatest { tracks ->
                _favoriteTracks.value = tracks
            }
        }
    }
}
