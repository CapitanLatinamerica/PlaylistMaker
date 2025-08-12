package com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.playlists.PlaylistInteractor
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.collections.map

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val _navigateToCreate = MutableLiveData(false)
    val navigateToCreate: LiveData<Boolean> = _navigateToCreate

    private val _playlists = MutableLiveData<List<PlaylistUi>>()
    val playlists: MutableLiveData<List<PlaylistUi>> = _playlists

    fun onCreatePlaylistClicked() {
        _navigateToCreate.value = true
    }

    fun onNavigationHandled() {
        _navigateToCreate.value = false
    }

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

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylistById(playlistId)
        }
    }

}