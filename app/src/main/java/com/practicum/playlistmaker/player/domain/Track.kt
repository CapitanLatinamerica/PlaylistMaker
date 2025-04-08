package com.practicum.playlistmaker.player.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
@Parcelize
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
    val previewUrl: String? = null      // Ссылка на отрывок трека
) : Parcelable {
    val trackTime: String
        get() {
            val dateFormat = SimpleDateFormat("m:ss", Locale.getDefault())
            return dateFormat.format(trackTimeMillis)
        }

/*    val artworkUrl512: String
        get() = artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg") // Высокое качество*/

    val releaseYear: String?
        get() = releaseDate?.takeIf { it.isNotEmpty() }?.split("-")?.get(0) // Извлечение года

    fun getArtworkUrl512(): String {
        return artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg")
    }
}

