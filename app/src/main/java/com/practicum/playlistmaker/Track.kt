import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val trackId: Int,         // Уникальный идентификатор трека
    val trackName: String,    // Название композиции
    val artistName: String,   // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека в миллисекундах
    val artworkUrl100: String  // Ссылка на изображение обложки
) {
    val trackTime: String
        get() {
            val dateFormat = SimpleDateFormat("m:ss", Locale.getDefault())
            return dateFormat.format(trackTimeMillis)
        }

    fun trimmed(): Track {
        return copy(
            trackName = trackName.trim(),
            artistName = artistName.trim()
        )
    }
}

