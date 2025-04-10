package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.player.domain.Track

interface SearchRepository {
    suspend fun searchTracks(query: String): List<Track>  // Метод поиска треков
}

