package com.practicum.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackName: String,  // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long,  // Продолжительность трека в миллисекундах
    val artworkUrl100: String   // Ссылка на изображение обложки
) {
    val trackTime: String
        get() {
            val dateFormat = SimpleDateFormat("m:ss", Locale.getDefault())
            return dateFormat.format(trackTimeMillis)
        }
}
