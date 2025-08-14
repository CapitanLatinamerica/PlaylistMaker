package com.practicum.playlistmaker.db.domain.playlists

import com.practicum.playlistmaker.db.data.playlists.PlaylistEntity
import com.practicum.playlistmaker.media.fragments.playlists.domain.PlaylistsRepository
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.network.ITunesService
import kotlinx.coroutines.flow.Flow

class PlaylistInteractor(
    private val repository: PlaylistsRepository,
    private val iTunesService: ITunesService
) {
    suspend fun createPlaylist(playlist: PlaylistEntity) {
        repository.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: PlaylistEntity) {
        repository.updatePlaylist(playlist)
    }

    suspend fun getPlaylistById(id: Int): PlaylistEntity? {
        return repository.getPlaylistById(id)
    }

    suspend fun getAllPlaylistsFlow(): Flow<List<PlaylistEntity>> {
        return repository.getAllPlaylistsFlow()
    }

    suspend fun addTrackToPlaylist(playlistId: Int, track: Track): Boolean {
        return repository.addTrackToPlaylist(playlistId, track)
    }

    suspend fun getTracksForPlaylist(playlist: PlaylistEntity): List<Track> {
        if (playlist.trackIds.isBlank()) return emptyList()
        val ids = playlist.trackIds.split(",").mapNotNull { it.toLongOrNull() }
        if (ids.isEmpty()) return emptyList()

        // Формируем строку ID через запятую
        val idsString = ids.joinToString(",")

        return try {
            val response = iTunesService.lookupTracksByIds(idsString)
            response.results.map { it.toDomain() } // Реализуйте метод toDomain() для TrackDto -> Track
        } catch (e: Exception) {
            emptyList() // Обработка ошибки, например, пустой список
        }
    }

    suspend fun deletePlaylistById(playlistId: Int) {
        repository.deletePlaylistById(playlistId)
    }

    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Long) {
        repository.deleteTrackFromPlaylist(playlistId, trackId)
    }
}
