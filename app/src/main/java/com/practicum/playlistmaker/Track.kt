package com.practicum.playlistmaker

data class Track(
    val trackName: String,  // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long,  // Продолжительность трека в миллисекундах
    val artworkUrl100: String   // Ссылка на изображение обложки
) {
    val trackTime: String
        get() {
            val minutes = (trackTimeMillis / 1000) / 60
            val seconds = (trackTimeMillis / 1000) % 60
            return String.format("%d:%02d", minutes, seconds)
        }
}
