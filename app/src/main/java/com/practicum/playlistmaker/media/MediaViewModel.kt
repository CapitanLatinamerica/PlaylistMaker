package com.practicum.playlistmaker.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaViewModel : ViewModel() {

    private val _currentTab = MutableLiveData<Int>(0)
    val currentTab: LiveData<Int> get() = _currentTab

    fun setCurrentTab(tabPosition: Int) {
        _currentTab.value = tabPosition
    }
}