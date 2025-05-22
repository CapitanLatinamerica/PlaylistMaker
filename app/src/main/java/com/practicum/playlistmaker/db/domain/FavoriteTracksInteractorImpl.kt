package com.practicum.playlistmaker.db.domain

import com.practicum.playlistmaker.db.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTracksRepository.addTrackToFavorites(track)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteTracksRepository.removeTrackFromFavorites(track)
    }

    override suspend fun getAllFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getAllFavoriteTracks()
    }
}
