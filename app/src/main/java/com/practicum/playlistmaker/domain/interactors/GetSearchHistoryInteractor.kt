package com.practicum.playlistmaker.domain.interactors  //Пакет для интеракторов

import com.practicum.playlistmaker.player.domain.Track  //Импортируем Track

interface GetSearchHistoryInteractor {
    suspend fun getSearchHistory(): List<Track>                                                     //Метод для получения истории поиска
}
