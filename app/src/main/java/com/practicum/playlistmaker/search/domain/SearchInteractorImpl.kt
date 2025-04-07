package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.SearchRepository

class SearchInteractorImpl(
    private val searchRepository: SearchRepository
) : SearchInteractor {

    // Реализуем метод для поиска треков
    override suspend fun searchTracks(query: String): List<Track> {
        return searchRepository.searchTracks(query)  // Получаем список треков из репозитория
    }
}

