package com.practicum.playlistmaker.db.data.playlists

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {

    @Query("SELECT * FROM playlists WHERE trackIds IN (:trackId)")
    fun getTracksByIds(trackId: List<String>): Flow<List<TrackEntity>>

    @Query("DELETE FROM playlists WHERE trackIds = :trackId")
    suspend fun deleteTrack(trackId: String)
}