package com.practicum.playlistmaker.domain.interactors  //Пакет для интеракторов

import com.practicum.playlistmaker.player.domain.Track  //Импортируем Track

interface SearchTracksInteractor {
    suspend fun searchTracks(query: String): List<Track>                                            //Метод для поиска треков по запросу
}
