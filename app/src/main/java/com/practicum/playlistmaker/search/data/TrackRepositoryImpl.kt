package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.network.ITunesSearchResponse
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.domain.SearchHistory
import com.practicum.playlistmaker.search.domain.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Реализация репозитория для работы с треками
class TrackRepositoryImpl(
    private val iTunesService: ITunesService,
    private val searchHistory: SearchHistory
) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        // Используем корутину для асинхронной работы
        return withContext(Dispatchers.IO) {
            try {
                val response: ITunesSearchResponse = iTunesService.searchSongs(query)  // Теперь вызов асинхронный
                if (response.resultCount > 0) {
                    // Если запрос успешен, преобразуем данные и передаем их
                    val tracks = response.results.map { it.toDomain() }  // Преобразуем DTO в Domain
                    tracks
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()  // Возвращаем пустой список в случае ошибки
            }
        }
    }

    override suspend fun saveSearchHistory(track: Track) {
        searchHistory.addTrack(track)  // Сохраняем трек в историю
    }

    override suspend fun getSearchHistory(): List<Track> {
        return searchHistory.getHistory()  // Получаем историю поиска
    }
}