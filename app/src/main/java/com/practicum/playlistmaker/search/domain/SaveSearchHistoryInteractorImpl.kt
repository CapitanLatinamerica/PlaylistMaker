package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track

class SaveSearchHistoryInteractorImpl(
    private val searchHistory: SearchHistory
) : SaveSearchHistoryInteractor {

    override suspend fun saveTrackToHistory(track: Track) {
        searchHistory.addTrack(track)  // Добавляем трек в историю
    }

    override suspend fun clearSearchHistory() {
        searchHistory.clearHistory()  // Очищаем историю
    }
}