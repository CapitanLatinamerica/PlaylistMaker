package com.practicum.playlistmaker.db.data.favorites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)     //добавляет или обновляет трек (если trackId совпадает)

    @Delete
    suspend fun deleteTrack(track: FavoriteTrackEntity)     //удаляет трек

    @Query("SELECT * FROM favorite_tracks ORDER BY trackId DESC")
    fun getAllTracks(): Flow<List<FavoriteTrackEntity>>      //возвращает список всех избранных треков

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getAllTrackIds(): List<Long>            //возвращает только идентификаторы избранных треков

    @Query("SELECT * FROM favorite_tracks WHERE trackId = :trackId LIMIT 1")
    fun getTrackById(trackId: Long): FavoriteTrackEntity?

    @Query("UPDATE favorite_tracks SET addedAt = :addedAt WHERE trackId = :trackId")
    suspend fun updateTrackAddedAt(trackId: Long, addedAt: Long)
}
