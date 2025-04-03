package com.practicum.playlistmaker.domain.interactors                                              //Пакет для интеракторов

import com.practicum.playlistmaker.domain.repository.TrackRepository                                //Импортируем репозиторий
import com.practicum.playlistmaker.player.domain.Track                                                     //Импортируем Track

class SearchTracksInteractorImpl(
    private val trackRepository: TrackRepository                                                    //Репозиторий для работы с треками
) : SearchTracksInteractor {

    //Реализуем метод поиска треков
    override suspend fun searchTracks(query: String): List<Track> {
        return trackRepository.searchTracks(query)                                                  //Получаем результаты поиска из репозитория
    }
}
