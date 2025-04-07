package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track

interface SaveSearchHistoryInteractor {
    suspend fun saveTrackToHistory(track: Track)                                                    //Метод для сохранения трека в историю
    suspend fun clearSearchHistory()
}