package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.player.domain.Track

interface SearchInteractor {
    suspend fun searchTracks(query: String): List<Track>  // Возвращаем List<Track> вместо callback
}