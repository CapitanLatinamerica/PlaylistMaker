package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.domain.interactor.GetSearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.repository.TrackRepository

class GetSearchHistoryInteractorImpl(
    private val trackRepository: TrackRepository                                                    //Репозиторий для работы с историей поиска
) : GetSearchHistoryInteractor {

    override suspend fun getSearchHistory(): List<Track> {                                          //Реализуем метод для получения истории поиска
        return trackRepository.getSearchHistory()                                                   //Получаем историю из репозитория
    }
}