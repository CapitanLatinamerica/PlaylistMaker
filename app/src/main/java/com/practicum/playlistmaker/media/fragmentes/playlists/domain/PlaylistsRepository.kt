package com.practicum.playlistmaker.media.fragmentes.playlists.domain

interface PlaylistsRepository {
    suspend fun createPlaylist(name: String, description: String)
}