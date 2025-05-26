package com.practicum.playlistmaker.player.domain

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String? = null, // Альбом
    val releaseDate: String? = null,    // Год
    val primaryGenreName: String? = null, // Жанр
    val country: String? = null,         // Страна исполнителя
    val previewUrl: String? = null,
    var isFavorite: Boolean = false // состояние лайка
) {

    fun getArtworkUrl512(): String {
        return artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg")
    }
}

