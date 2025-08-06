package com.practicum.playlistmaker.db.domain.favorites

import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(track: Track)
    suspend fun getAllFavoriteTracks(): Flow<List<Track>>
}
