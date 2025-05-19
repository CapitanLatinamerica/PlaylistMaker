package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.domain.interactor.SearchInteractor
import com.practicum.playlistmaker.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchInteractorImpl(
    private val searchRepository: SearchRepository
) : SearchInteractor {

    override suspend fun searchTracks(query: String): Flow<Result<List<Track>>> {
        return searchRepository.searchTracks(query)
    }
}
