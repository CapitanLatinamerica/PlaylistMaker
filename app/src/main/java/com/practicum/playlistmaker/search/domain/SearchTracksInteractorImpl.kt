package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track

class SearchTracksInteractorImpl(
    private val trackRepository: TrackRepository                                                    //Репозиторий для работы с треками
) : SearchTracksInteractor {

    //Реализуем метод поиска треков
    override suspend fun searchTracks(query: String): List<Track> {
        return trackRepository.searchTracks(query)                                                  //Получаем результаты поиска из репозитория
    }
}