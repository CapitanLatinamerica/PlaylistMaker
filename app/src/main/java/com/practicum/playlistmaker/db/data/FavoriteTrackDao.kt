package com.practicum.playlistmaker.db.data

import androidx.room.*
import com.practicum.playlistmaker.db.domain.model.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: com.practicum.playlistmaker.db.domain.model.FavoriteTrackEntity)     //добавляет или обновляет трек (если trackId совпадает)

    @Delete
    suspend fun deleteTrack(track: com.practicum.playlistmaker.db.domain.model.FavoriteTrackEntity)     //удаляет трек

    @Query("SELECT * FROM favorite_tracks ORDER BY trackId DESC")
    suspend fun getAllTracks(): Flow<List<FavoriteTrackEntity>>       //возвращает список всех избранных треков

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getAllTrackIds(): List<Long>            //возвращает только идентификаторы избранных треков
}
