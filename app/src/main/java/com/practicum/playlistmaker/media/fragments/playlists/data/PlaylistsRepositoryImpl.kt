package com.practicum.playlistmaker.media.fragments.playlists.data

import com.practicum.playlistmaker.db.data.playlists.PlaylistDao
import com.practicum.playlistmaker.db.data.playlists.PlaylistEntity
import com.practicum.playlistmaker.media.fragments.playlists.domain.PlaylistsRepository
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsRepositoryImpl(private val dao: PlaylistDao) : PlaylistsRepository {
    override suspend fun createPlaylist(name: String, description: String?, coverPath: String?): Long {
        val newPlaylist = PlaylistEntity(
            name = name,
            description = description.toString(),
            coverPath = coverPath,
            trackIds = "",
            trackCount = 0
        )
        return dao.insertPlaylist(newPlaylist)
    }

    override suspend fun insertPlaylist(playlist: PlaylistEntity) {
        dao.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        dao.updatePlaylist(playlist)
    }

    override suspend fun getPlaylistById(id: Int): PlaylistEntity? {
        return dao.getPlaylistById(id)
    }
    override suspend fun getAllPlaylistsFlow(): Flow<List<PlaylistEntity>> {
        return dao.getAllPlaylistsFlow()
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, track: Track): Boolean {
        val playlist = dao.getPlaylistById(playlistId) ?: return false
        val trackId = track.trackId.toString()

        // Чистим строку и проверяем на пустоту
        val existingTrackIds = if (playlist.trackIds.isBlank()) {
            mutableListOf()
        } else {
            playlist.trackIds.split(",").toMutableList()
        }

        if (trackId in existingTrackIds) return false // уже есть

        existingTrackIds.add(trackId)
        val updatedTrackIds = existingTrackIds.joinToString(",")
        val updatedTrackCount = existingTrackIds.size

        dao.updateTracks(playlistId, updatedTrackIds, updatedTrackCount)

        return true
    }

}