package com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.playlists.PlaylistInteractor
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistInteractor
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
            val entities = interactor.getAllPlaylists()
            val uiList = entities.map {
                PlaylistUi(
                    id = it.id,
                    name = it.name,
                    description = it.description ?: "",
                    coverPath = it.coverPath,
                    trackCount = it.trackCount
                )
            }
            _playlists.value = uiList
        }
    }
}