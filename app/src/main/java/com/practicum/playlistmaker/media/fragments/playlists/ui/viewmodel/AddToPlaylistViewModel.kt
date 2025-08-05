package com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.playlists.PlaylistInteractor
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AddToPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val trackToAdd: Track
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<PlaylistUi>>(emptyList())
    val playlists: StateFlow<List<PlaylistUi>> = _playlists.asStateFlow()

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylistsFlow()
                .map { list ->
                    list.map { playlist ->
                        PlaylistUi(
                            id = playlist.id,
                            name = playlist.name,
                            description = playlist.description,
                            coverPath = playlist.coverPath,
                            trackCount = playlist.trackCount
                        )
                    }
                }
                .collect { playlistUiList ->
                    _playlists.value = playlistUiList
                }
        }
    }

    fun addTrackToPlaylist(playlistId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = playlistInteractor.addTrackToPlaylist(playlistId, trackToAdd)
            onResult(result)
        }
    }
}

