package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track

interface SearchTracksInteractor {
    suspend fun searchTracks(query: String): List<Track>                                            //Метод для поиска треков по запросу
}