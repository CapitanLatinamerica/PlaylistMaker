package com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.playlists.PlaylistInteractor
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyAwesomePlaylistFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlistDetails = MutableLiveData<PlaylistUi>()
    val playlistDetails: LiveData<PlaylistUi> = _playlistDetails

    private val _tracksStateFlow = MutableStateFlow<List<Track>>(emptyList())
    val tracksStateFlow: StateFlow<List<Track>> = _tracksStateFlow

    fun loadPlaylistDetails(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            _playlistDetails.value = PlaylistUi(
                id = playlist?.id ?: 0,
                name = playlist?.name ?: "No name",
                description = playlist?.description,
                coverPath = playlist?.coverPath,
                trackCount = playlist?.trackCount ?: 0
            )
        }
    }

    private fun loadPlaylistTracks(playlistEntity: com.practicum.playlistmaker.db.data.playlists.PlaylistEntity) {
        viewModelScope.launch {
            try {
                val tracks = playlistInteractor.getTracksForPlaylist(playlistEntity)
                _tracksStateFlow.value = tracks
            } catch (e: Exception) {
                _tracksStateFlow.value = emptyList()
            }
        }
    }
}
