package com.practicum.playlistmaker.db.data.playlists
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity?

    @Query("SELECT * FROM playlists")
    fun getAllPlaylistsFlow(): Flow<List<PlaylistEntity>>

    @Query("UPDATE playlists SET trackIds = :trackIds, trackCount = :trackCount WHERE id = :playlistId")
    suspend fun updateTracks(playlistId: Int, trackIds: String, trackCount: Int)

}
