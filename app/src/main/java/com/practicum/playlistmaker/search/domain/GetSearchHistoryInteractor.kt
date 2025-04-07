package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track

interface GetSearchHistoryInteractor {
    suspend fun getSearchHistory(): List<Track>                                                     //Метод для получения истории поиска
}