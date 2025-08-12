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
            val playlistEntity = playlistInteractor.getPlaylistById(playlistId)
            if (playlistEntity != null) {
                _playlistDetails.value = PlaylistUi(
                    id = playlistEntity.id,
                    name = playlistEntity.name,
                    description = playlistEntity.description,
                    coverPath = playlistEntity.coverPath,
                    trackCount = playlistEntity.trackCount
                )
                loadPlaylistTracks(playlistEntity)
            }
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

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylistById(playlistId)
        }
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        val playlistId = _playlistDetails.value?.id ?: return

        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistId, trackId)
            // Обновляем данные плейлиста и треков после удаления
            val updatedPlaylist = playlistInteractor.getPlaylistById(playlistId)
            if (updatedPlaylist != null) {
                _playlistDetails.value = PlaylistUi(
                    id = updatedPlaylist.id,
                    name = updatedPlaylist.name,
                    description = updatedPlaylist.description,
                    coverPath = updatedPlaylist.coverPath,
                    trackCount = updatedPlaylist.trackCount
                )
                loadPlaylistTracks(updatedPlaylist)
            }
        }
    }
}
