package com.practicum.playlistmaker.media.fragmentes.playlists.data

import com.practicum.playlistmaker.db.data.playlists.Playlist
import com.practicum.playlistmaker.db.data.playlists.PlaylistDao
import com.practicum.playlistmaker.media.fragmentes.playlists.domain.PlaylistsRepository

class PlaylistsRepositoryImpl(private val playlistDao: PlaylistDao) : PlaylistsRepository {
    override suspend fun createPlaylist(name: String, description: String) {
        val newPlaylist = Playlist(
            name = name,
            description = description,
            trackIds = "[]", // Пустой список треков
            trackCount = 0
        )
        playlistDao.insert(newPlaylist)
    }
}