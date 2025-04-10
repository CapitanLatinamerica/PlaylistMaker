package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.player.domain.Track

interface SearchHistoryRepository {
    fun saveTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}
