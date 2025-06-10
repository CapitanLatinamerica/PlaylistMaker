package com.practicum.playlistmaker.db.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.player.domain.Track

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(
    @PrimaryKey val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    val addedAt: Long = 0
) {
    // Функция для преобразования в Track
    fun toTrack(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
            addedAt = addedAt
        )
    }

    companion object {
        // Функция для преобразования из Track в FavoriteTrackEntity
        fun fromTrack(track: Track): FavoriteTrackEntity {
            return FavoriteTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                addedAt = track.addedAt
            )
        }
    }
}
