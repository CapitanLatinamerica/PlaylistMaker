package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.SearchRepository

class SearchInteractor(private val searchRepository: SearchRepository) {

    fun searchTracks(query: String, callback: (List<Track>) -> Unit) {
        searchRepository.searchTracks(query, callback)
    }
}
