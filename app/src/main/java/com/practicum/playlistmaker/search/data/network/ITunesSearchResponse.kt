package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.TrackDto

// Ответ от API iTunes
data class ITunesSearchResponse(
    val resultCount: Int,         // Количество найденных результатов
    val results: List<TrackDto>   // Список треков в формате DTO
)