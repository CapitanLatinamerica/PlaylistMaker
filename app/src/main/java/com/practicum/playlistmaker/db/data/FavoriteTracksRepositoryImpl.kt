package com.practicum.playlistmaker.db.data.repository

import android.util.Log
import com.practicum.playlistmaker.db.data.FavoriteTrackDao
import com.practicum.playlistmaker.db.domain.model.FavoriteTrackEntity
import com.practicum.playlistmaker.db.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val favoriteTracksDao: FavoriteTrackDao
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        Log.d("FavoriteTracksRepositoryImpl", "Inserting track into database: ${track.trackName}")
        favoriteTracksDao.insertTrack(FavoriteTrackEntity.fromTrack(track))
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        Log.d("FavoriteTracksRepositoryImpl", "Deleting track from database: ${track.trackName}")
        favoriteTracksDao.deleteTrack(FavoriteTrackEntity.fromTrack(track))
    }

    override suspend fun getAllFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksDao.getAllTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }
}

