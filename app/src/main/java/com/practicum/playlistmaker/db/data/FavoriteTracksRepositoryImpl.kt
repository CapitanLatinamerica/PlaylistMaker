package com.practicum.playlistmaker.db.data.repository

import com.practicum.playlistmaker.db.data.FavoriteTrackDao
import com.practicum.playlistmaker.db.data.FavoriteTrackEntity
import com.practicum.playlistmaker.db.domain.FavoriteTracksRepository
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val favoriteTracksDao: FavoriteTrackDao
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTracksDao.insertTrack(FavoriteTrackEntity.fromTrack(track))
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteTracksDao.deleteTrack(FavoriteTrackEntity.fromTrack(track))
    }

    override suspend fun getAllFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksDao.getAllTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }
}

