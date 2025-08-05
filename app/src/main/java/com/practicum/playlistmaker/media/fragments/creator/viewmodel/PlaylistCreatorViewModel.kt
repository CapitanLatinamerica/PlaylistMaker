package com.practicum.playlistmaker.media.fragments.creator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.app.SingleLiveEvent
import com.practicum.playlistmaker.media.fragments.playlists.domain.PlaylistsRepository

class PlaylistCreatorViewModel(
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {

    private val _shouldCloseScreen = MutableLiveData(false)
    val shouldCloseScreen: LiveData<Boolean> = _shouldCloseScreen

    private val _showExitDialog = SingleLiveEvent<Unit>()
    val showExitDialog: LiveData<Unit> = _showExitDialog

    private var imageSelected = false

    fun isImageSelected(): Boolean = imageSelected

    fun onBackPressed(title: String, description: String, isImageSet: Boolean) {
        val isAllEmpty = title.isBlank() && description.isBlank() && !isImageSet
        if (isAllEmpty) {
            _shouldCloseScreen.value = true
        } else {
            _showExitDialog.value = Unit
        }
    }

    fun confirmExit() {
        _shouldCloseScreen.value = true
    }

    fun resetCloseFlag() {
        _shouldCloseScreen.value = false
    }

    suspend fun createPlaylist(name: String, description: String?, coverPath: String?): Long {
        return playlistsRepository.createPlaylist(
            name = name,
            description = description,
            coverPath = coverPath
        )
        _shouldCloseScreen.value = true
    }
}