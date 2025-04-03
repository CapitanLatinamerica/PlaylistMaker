package com.practicum.playlistmaker.domain.repository  // Пакет для репозиториев

import com.practicum.playlistmaker.player.domain.Track  // Импортируем Track из Domain слоя

interface TrackRepository {                                                                         // Интерфейс репозитория для работы с треками

    suspend fun searchTracks(query: String): List<Track>                                            // Функция для поиска треков по ключевому слову
    suspend fun saveSearchHistory(track: Track)                                                     // метод для сохранения в историю
    suspend fun getSearchHistory(): List<Track>
}
