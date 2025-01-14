import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String? = null, // Альбом
    val releaseYear: String? = null,    // Год
    val genre: String? = null,          // Жанр
    val country: String? = null         // Страна исполнителя
) {
    val trackTime: String
        get() {
            val dateFormat = SimpleDateFormat("m:ss", Locale.getDefault())
            return dateFormat.format(trackTimeMillis)
        }

    val artworkUrl512: String
        get() = artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg") // Высокое качество

    fun trimmed(): Track {
        return copy(
            trackName = trackName.trim(),
            artistName = artistName.trim(),
            collectionName = collectionName?.trim(),
            genre = genre?.trim(),
            country = country?.trim()
        )
    }

    fun getName(): String {
        return trackName
    }

    fun getArtist(): String {
        return artistName
    }

    fun getTime(): Long {
        return trackTimeMillis
    }
}

