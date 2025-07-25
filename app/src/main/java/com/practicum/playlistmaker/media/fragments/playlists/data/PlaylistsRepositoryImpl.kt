package com.practicum.playlistmaker.media.fragments.playlists.data

import com.practicum.playlistmaker.db.data.playlists.PlaylistEntity
import com.practicum.playlistmaker.db.data.playlists.PlaylistDao
import com.practicum.playlistmaker.media.fragments.playlists.domain.PlaylistsRepository
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistItem

class PlaylistsRepositoryImpl(private val dao: PlaylistDao) : PlaylistsRepository {
    override suspend fun createPlaylist(name: String, description: String?, coverPath: String?) {
       val newPlaylist = PlaylistEntity(
           name = name,
           description = description.toString(),
           coverPath = coverPath,
           trackIds = "[]",
           trackCount = 0
        )
        dao.insertPlaylist(newPlaylist)
    }

    override suspend fun insertPlaylist(playlist: PlaylistEntity) {
        dao.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        dao.updatePlaylist(playlist)
    }

    override suspend fun getAllPlaylists(): List<PlaylistEntity> {
        return dao.getAllPlaylists()
    }

    override suspend fun getPlaylistById(id: Int): PlaylistEntity? {
        return dao.getPlaylistById(id)
    }
}