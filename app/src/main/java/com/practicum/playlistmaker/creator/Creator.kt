package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.domain.SearchInteractorImpl
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.SearchInteractor
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.SearchRepository

object Creator {

    // Создаем репозиторий для истории поиска
    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPreferences)
    }

    // Создаем интерактор для истории поиска
    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractor(provideSearchHistoryRepository(context))
    }

    // Создаем интерактор для поиска
    fun provideSearchInteractor(context: Context): SearchInteractor {
        val iTunesService = ITunesService.create()
        val searchRepository: SearchRepository = SearchRepositoryImpl(iTunesService)
        return SearchInteractorImpl(searchRepository)
    }
}
