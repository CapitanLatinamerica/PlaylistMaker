package com.practicum.playlistmaker.db.domain

import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(track: Track)
    suspend fun getAllFavoriteTracks(): Flow<List<Track>>
}
