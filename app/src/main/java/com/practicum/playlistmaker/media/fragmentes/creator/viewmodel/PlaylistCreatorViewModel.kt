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

    private val _shouldCloseScreen = MutableLiveData(false)
    val shouldCloseScreen: LiveData<Boolean> = _shouldCloseScreen

    private var imageSelected = false

    fun setImageSelected(selected: Boolean) {
        imageSelected = selected
    }
    fun isImageSelected(): Boolean = imageSelected

    fun onBackPressed(title: String, description: String, isImageSet: Boolean) {
        val isAllEmpty = title.isBlank() && description.isBlank() && !isImageSet
        if (isAllEmpty) {
            _shouldCloseScreen.value = true
        } else {
            // Позже можно показать диалог — сейчас просто ничего не делаем
        }
    }

}