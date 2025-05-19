package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(query: String): Flow<Result<List<Track>>>  // Метод поиска треков
}

