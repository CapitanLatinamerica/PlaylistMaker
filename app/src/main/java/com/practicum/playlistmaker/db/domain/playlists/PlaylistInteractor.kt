package com.practicum.playlistmaker.db.domain.playlists


import com.practicum.playlistmaker.db.data.playlists.PlaylistEntity
import com.practicum.playlistmaker.media.fragments.playlists.domain.PlaylistsRepository

class PlaylistInteractor(private val repository: PlaylistsRepository) {
    suspend fun createPlaylist(playlist: PlaylistEntity) {
        repository.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: PlaylistEntity) {
        repository.updatePlaylist(playlist)
    }

    suspend fun getAllPlaylists(): List<PlaylistEntity> {
        return repository.getAllPlaylists()
    }

    suspend fun getPlaylist(id: Int): PlaylistEntity? {
        return repository.getPlaylistById(id)
    }
}
