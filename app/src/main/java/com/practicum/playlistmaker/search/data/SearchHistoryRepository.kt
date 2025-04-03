package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.player.domain.Track

interface SearchHistoryRepository {

    fun saveTrack(track: Track)

    fun getHistory(): List<Track>

    fun clearHistory()
}
