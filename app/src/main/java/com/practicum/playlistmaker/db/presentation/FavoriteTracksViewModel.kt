package com.practicum.playlistmaker.media.fragmentes.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.FavoriteTracksInteractor
import com.practicum.playlistmaker.player.data.repository.LikeStorage
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    // Метод для загрузки всех избранных треков
    val favoriteTracksLiveData = MutableLiveData<List<Track>>()

    // Получаем Flow<List<Track>>
    private val _favoriteTracks = MutableStateFlow<List<Track>>(emptyList())
    val favoriteTracks: StateFlow<List<Track>> = _favoriteTracks

    init {
        observeFavoriteTracks()
    }

    fun loadFavoriteTracks() {
        viewModelScope.launch {
            try {
                favoriteTracksInteractor.getAllFavoriteTracks().collect { tracks ->
                    favoriteTracksLiveData.postValue(tracks) // Обновляем данные в LiveData
                }
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }

    // Метод для добавления трека в избранное
    fun addTrackToFavorites(track: Track) {
        viewModelScope.launch {
            favoriteTracksInteractor.addTrackToFavorites(track)
            Log.d("FavoriteTracksViewModel", "Updated favorite tracks: ${_favoriteTracks.value.size}")
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
                // Проверка на пустой список
                if (tracks.isEmpty()) {
                    Log.d("FavoriteTracks", "No favorite tracks found in database.")
                } else {
                    Log.d("FavoriteTracks", "Favorite tracks loaded, count: ${tracks.size}")
                }
                _favoriteTracks.value = tracks
                Log.d("FavoriteTracksViewModel", "Tracks loaded: ${tracks.size} items")
            }
        }
    }
}
