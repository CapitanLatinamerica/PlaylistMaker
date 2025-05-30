package com.practicum.playlistmaker.db.data

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
}
