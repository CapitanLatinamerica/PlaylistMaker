package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.network.ITunesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepositoryImpl(
    private val iTunesService: ITunesService  // Сервис для выполнения запросов
) : SearchRepository {

    // Реализация поиска треков
    override suspend fun searchTracks(query: String): List<Track> {
        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesService.searchSongs(query)
                response.results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
