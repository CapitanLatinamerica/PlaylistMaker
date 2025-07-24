package com.practicum.playlistmaker.db.domain.playlists


import com.practicum.playlistmaker.db.data.playlists.Playlist
import com.practicum.playlistmaker.media.fragmentes.playlists.domain.PlaylistsRepository

class PlaylistInteractor(private val repository: PlaylistsRepository) {
    suspend fun createPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    suspend fun getAllPlaylists(): List<Playlist> {
        return repository.getAllPlaylists()
    }

    suspend fun getPlaylist(id: Int): Playlist? {
        return repository.getPlaylistById(id)
    }
}
