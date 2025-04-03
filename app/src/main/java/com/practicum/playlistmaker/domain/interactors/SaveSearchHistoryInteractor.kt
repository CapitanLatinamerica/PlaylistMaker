package com.practicum.playlistmaker.domain.interactors  //Пакет для интеракторов

import com.practicum.playlistmaker.player.domain.Track  //Импортируем Track

interface SaveSearchHistoryInteractor {
    suspend fun saveTrackToHistory(track: Track)                                                    //Метод для сохранения трека в историю
    suspend fun saveSearchHistory(track: Track)
    suspend fun clearSearchHistory()
}
