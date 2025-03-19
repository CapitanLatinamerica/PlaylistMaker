package com.practicum.playlistmaker.domain.interactors                                              //Пакет для интеракторов

import com.practicum.playlistmaker.domain.repository.TrackRepository                                //Импортируем репозиторий
import com.practicum.playlistmaker.domain.Track                                                     //Импортируем Track

class GetSearchHistoryInteractorImpl(
    private val trackRepository: TrackRepository                                                    //Репозиторий для работы с историей поиска
) : GetSearchHistoryInteractor {

    override suspend fun getSearchHistory(): List<Track> {                                          //Реализуем метод для получения истории поиска
        return trackRepository.getSearchHistory()                                                   //Получаем историю из репозитория
    }
}
