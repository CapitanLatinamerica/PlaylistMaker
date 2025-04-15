package com.practicum.playlistmaker.main.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.main.domain.NaviInteractor

class MainViewModel(private val naviInteractor: NaviInteractor) : ViewModel() {
    fun onSearchClicked() = naviInteractor.openSearch()
    fun onMediaClicked() = naviInteractor.openMedia()
    fun onSettingsClicked() = naviInteractor.openSettings()
}