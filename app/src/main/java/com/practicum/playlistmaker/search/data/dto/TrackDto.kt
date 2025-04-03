package com.practicum.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.player.domain.Track

data class TrackDto(
    @SerializedName("trackId") val trackId: Long,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long,
    @SerializedName("artworkUrl100") val artworkUrl100: String,
    @SerializedName("collectionName") val collectionName: String? = null, // Альбом
    @SerializedName("releaseDate") val releaseDate: String? = null,    // Год
    @SerializedName("primaryGenreName") val primaryGenreName: String? = null, // Жанр
    @SerializedName("country") val country: String? = null,         // Страна исполнителя
    @SerializedName("previewUrl") val previewUrl: String? = null      // Ссылка на отрывок трека
) {
    fun toDomain(): Track {
        return Track(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            artworkUrl100 = this.artworkUrl100,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            previewUrl = this.previewUrl
        )
    }
}

