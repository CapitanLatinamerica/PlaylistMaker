package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.domain.Track

class SaveSearchHistoryInteractorImpl(
    private val searchHistory: SearchHistory
) : SaveSearchHistoryInteractor {

    override suspend fun saveTrackToHistory(track: Track) {
        searchHistory.addTrack(track)  // Добавляем трек в историю
    }

    override suspend fun saveSearchHistory(track: Track) {
        searchHistory.addTrack(track)  // Добавляем трек в историю
    }

    override suspend fun clearSearchHistory() {
        searchHistory.clearHistory()  // Очищаем историю
    }
}