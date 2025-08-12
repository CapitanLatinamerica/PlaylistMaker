package com.practicum.playlistmaker.media.fragments.playlists.domain

import com.practicum.playlistmaker.db.data.playlists.PlaylistEntity
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String?, coverPath: String?): Long
    suspend fun insertPlaylist(playlist: PlaylistEntity)
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun getAllPlaylistsFlow(): Flow<List<PlaylistEntity>>
    suspend fun getPlaylistById(id: Int): PlaylistEntity?
    suspend fun addTrackToPlaylist(playlistId: Int, track: Track): Boolean
    suspend fun deletePlaylistById(playlistId: Int)
    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Long)
}