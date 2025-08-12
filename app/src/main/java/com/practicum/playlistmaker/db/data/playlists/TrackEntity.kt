package com.practicum.playlistmaker.db.data.playlists

import androidx.room.PrimaryKey
import com.practicum.playlistmaker.db.data.favorites.FavoriteTrackEntity
import com.practicum.playlistmaker.player.domain.Track

data class TrackEntity(
    @PrimaryKey val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) {
    companion object {
        // Функция для преобразования из Track в TrackEntity
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
                previewUrl = track.previewUrl
            )
        }
    }

    fun TrackEntity.toDomain(): Track {
        return Track(
            this.trackId.toLong(),
            this.trackName,
            this.artistName,
            this.trackTimeMillis,
            this.artworkUrl100,
            this.collectionName,
            this.releaseDate,
            this.primaryGenreName,
            this.country,
            this.previewUrl
            )
    }
}
