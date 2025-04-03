package com.practicum.playlistmaker.data.mapper  // 📌 Новый пакет для мапперов

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.player.domain.Track

// Объект для преобразования данных между слоями
object TrackMapper {

    // Преобразует TrackDto (из Data слоя) в Track (из Domain слоя)
    fun map(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg"), // Улучшаем качество изображения
            collectionName = dto.collectionName ?: "Unknown Album",
            releaseDate = dto.releaseDate ?: "Unknown Year",
            primaryGenreName = dto.primaryGenreName ?: "Unknown Genre",
            country = dto.country ?: "Unknown Country",
            previewUrl = dto.previewUrl ?: ""
        )
    }

    // Преобразует список TrackDto в список Track
    fun mapList(dtoList: List<TrackDto>): List<Track> {
        return dtoList.map { map(it) } // Используем map для преобразования каждого элемента
    }
}
