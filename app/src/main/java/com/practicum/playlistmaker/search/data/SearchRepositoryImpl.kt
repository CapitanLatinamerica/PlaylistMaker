package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SearchRepositoryImpl(
    private val iTunesService: ITunesService  // Сервис для выполнения запросов
) : SearchRepository {

    // Реализация поиска треков
    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = iTunesService.searchSongs(query)
            val tracks = response.results.map {
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
                    previewUrl = it.previewUrl,
                    localId = 0
                )
            }
            emit(Result.success(tracks))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
