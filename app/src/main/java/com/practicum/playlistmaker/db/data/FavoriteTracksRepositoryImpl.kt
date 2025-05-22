package com.practicum.playlistmaker.db.data.repository

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
        // Преобразуем Track в FavoriteTrackEntity и добавляем в базу данных
        favoriteTracksDao.insertTrack(FavoriteTrackEntity.fromTrack(track))
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        // Преобразуем Track в FavoriteTrackEntity и удаляем из базы данных
        favoriteTracksDao.deleteTrack(FavoriteTrackEntity.fromTrack(track))
    }

    override suspend fun getAllFavoriteTracks(): Flow<List<Track>> {
        // Получаем все записи из базы и преобразуем их в Track
        return favoriteTracksDao.getAllTracks().map { entities ->
            entities.map { it.toTrack() } // Преобразуем каждую сущность в Track
        }
    }
}
