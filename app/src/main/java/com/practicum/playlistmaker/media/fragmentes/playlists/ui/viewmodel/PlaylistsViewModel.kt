package com.practicum.playlistmaker.media.fragmentes.playlists.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel : ViewModel() {
    private val _navigateToCreate = MutableLiveData(false)
    val navigateToCreate: LiveData<Boolean> = _navigateToCreate

    fun onCreatePlaylistClicked() {
        _navigateToCreate.value = true
    }

    fun onNavigationHandled() {
        _navigateToCreate.value = false
    }
}