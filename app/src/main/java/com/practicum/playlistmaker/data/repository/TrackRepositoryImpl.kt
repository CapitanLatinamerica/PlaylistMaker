package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.search.data.network.ITunesSearchResponse
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

// Реализация репозитория для работы с треками
class TrackRepositoryImpl(
    private val iTunesService: ITunesService,
    private val searchHistory: SearchHistory
) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        // Используем корутину для асинхронной работы
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<ITunesSearchResponse> = iTunesService.searchSongs(query).execute()  // Важно: execute() блокирует текущую корутину
                if (response.isSuccessful) {
                    // Если запрос успешен, преобразуем данные и передаем их
                    val tracks = response.body()?.results?.map { it.toDomain() } ?: emptyList()
                    tracks
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
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
