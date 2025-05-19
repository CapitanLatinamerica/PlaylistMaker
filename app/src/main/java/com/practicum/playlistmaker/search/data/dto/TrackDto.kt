package com.practicum.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

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
}

