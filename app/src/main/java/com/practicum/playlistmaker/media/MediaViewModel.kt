package com.practicum.playlistmaker.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaViewModel(
    private val mediaAdapter: MediaPagerAdapter
) : ViewModel() {

    private val _currentTab = MutableLiveData<Int>()
    val currentTab: LiveData<Int> get() = _currentTab

    fun setCurrentTab(tabPosition: Int) {
        _currentTab.value = tabPosition
    }

    fun getAdapter() = mediaAdapter
}
