package com.practicum.playlistmaker.media.fragmentes.creator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.fragmentes.playlists.domain.PlaylistsRepository
import kotlinx.coroutines.launch

class PlaylistCreatorViewModel(
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {

    private val _playlistName = MutableLiveData<String>()
    val playlistName: LiveData<String> = _playlistName

    private val _playlistDescription = MutableLiveData<String>()
    val playlistDescription: LiveData<String> = _playlistDescription

    fun setPlaylistName(name: String) {
        _playlistName.value = name
    }

    fun setPlaylistDescription(description: String) {
        _playlistDescription.value = description
    }

    fun createPlaylist() {
        val name = _playlistName.value ?: return
        val description = _playlistDescription.value ?: return

        viewModelScope.launch {
            playlistsRepository.createPlaylist(name, description)
        }
    }
}