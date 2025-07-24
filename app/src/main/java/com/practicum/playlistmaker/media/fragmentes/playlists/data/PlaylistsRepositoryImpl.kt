package com.practicum.playlistmaker.media.fragmentes.playlists.data

import android.content.Context
import com.practicum.playlistmaker.db.data.playlists.Playlist
import com.practicum.playlistmaker.db.data.playlists.PlaylistDao
import com.practicum.playlistmaker.media.fragmentes.playlists.domain.PlaylistsRepository

class PlaylistsRepositoryImpl(private val dao: PlaylistDao) : PlaylistsRepository {
    override suspend fun createPlaylist(name: String, description: String) {
/*        val newPlaylist = Playlist(
            name = name,
            description = description,
            trackIds = "[]", // Пустой список треков
            trackCount = 0
        )
        dao.insert(newPlaylist)*/
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        dao.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        dao.updatePlaylist(playlist)
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return dao.getAllPlaylists()
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        return dao.getPlaylistById(id)
    }
}