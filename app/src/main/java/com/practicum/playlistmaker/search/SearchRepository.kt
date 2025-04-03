package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.player.domain.Track

interface SearchRepository {
    fun searchTracks(query: String, callback: (List<Track>) -> Unit)
}
