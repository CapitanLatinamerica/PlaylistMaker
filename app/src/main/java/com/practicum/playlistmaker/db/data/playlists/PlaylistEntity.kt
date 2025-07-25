package com.practicum.playlistmaker.db.data.playlists

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val coverPath: String? = null,
    val trackIds: String,
    val trackCount: Int
)