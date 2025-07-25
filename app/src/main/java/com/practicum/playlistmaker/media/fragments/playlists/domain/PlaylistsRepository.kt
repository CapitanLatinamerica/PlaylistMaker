package com.practicum.playlistmaker.media.fragments.playlists.domain

import com.practicum.playlistmaker.db.data.playlists.PlaylistEntity

interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String?, coverPath: String?)
    suspend fun insertPlaylist(playlist: PlaylistEntity)
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    suspend fun getPlaylistById(id: Int): PlaylistEntity?
}