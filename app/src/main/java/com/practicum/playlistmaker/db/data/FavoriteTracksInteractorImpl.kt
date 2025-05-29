package com.practicum.playlistmaker.db.data

import android.util.Log
import com.practicum.playlistmaker.db.domain.FavoriteTracksInteractor
import com.practicum.playlistmaker.db.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun addTrackToFavorites(track: Track) {
        Log.d("FavoriteTracksInteractorImpl", "Adding track to favorites: ${track.trackName}")
        favoriteTracksRepository.addTrackToFavorites(track)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        Log.d("FavoriteTracksInteractorImpl", "Removing track from favorites: ${track.trackName}")
        favoriteTracksRepository.removeTrackFromFavorites(track)
    }

    override suspend fun getAllFavoriteTracks(): Flow<List<Track>> {
        Log.d("FavoriteTracksInteractorImpl", "Fetching all favorite tracks")
        return favoriteTracksRepository.getAllFavoriteTracks()
    }
}