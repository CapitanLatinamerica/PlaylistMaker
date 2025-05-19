package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    suspend fun searchTracks(query: String): Flow<Result<List<Track>>>
}