package com.practicum.playlistmaker.media

import androidx.lifecycle.ViewModel

class MediaViewModel(
    private val mediaAdapter: MediaPagerAdapter
) : ViewModel() {

    fun getAdapter() = mediaAdapter
}