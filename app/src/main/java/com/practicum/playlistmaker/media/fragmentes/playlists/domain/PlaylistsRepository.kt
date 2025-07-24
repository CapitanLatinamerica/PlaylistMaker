package com.practicum.playlistmaker.media.fragmentes.playlists.domain

import com.practicum.playlistmaker.db.data.playlists.Playlist

interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String)
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getAllPlaylists(): List<Playlist>
    suspend fun getPlaylistById(id: Int): Playlist?
}