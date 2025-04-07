package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.SearchHistoryRepository

class SearchHistoryInteractor(private val searchHistoryRepository: SearchHistoryRepository) {

    fun saveTrack(track: Track) {
        searchHistoryRepository.saveTrack(track)  // Сохраняем трек
    }

    fun getHistory(): List<Track> {
        return searchHistoryRepository.getHistory()  // Получаем историю
    }

    fun clearHistory() {
        searchHistoryRepository.clearHistory()  // Очищаем историю
    }
}

