package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import android.util.Log
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : SearchHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
    }

    private fun toJsonList(tracks: List<Track>): String {                                           // Функция для сериализации списка объектов в JSON
        return Json.encodeToString(tracks) // Кодируем список в JSON
    }

    fun fromJsonList(json: String?): List<Track> {                                                  // Функция для десериализации списка объектов из JSON
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                Json.decodeFromString<List<Track>>(json)                                            // Используем kotlinx.serialization для десериализации
            } catch (e: Exception) {
                emptyList()                                                                         // Возвращаем пустой список в случае ошибки
            }
        }
    }

    // Сохранение трека в историю
    override fun saveTrack(track: Track) {
        try {
            val history = getHistory().toMutableList()
            if (history.size >= 10) {
                history.removeAt(0)                                                           // Убираем самый старый элемент, если их больше 10
            }
            history.removeAll { existingTrack -> existingTrack.trackId == track.trackId }
            history.add(0, track)                                                             // Добавляем трек в начало списка

            sharedPreferences.edit().putString(HISTORY_KEY, toJsonList(history)).apply()            // Сохраняем историю в SharedPreferences
        } catch (e: Exception) {
            Log.e("SearchHistoryRepository", "Error saving track to history: ${e.message}", e)
        }
    }

    // Получение списка треков из SharedPreferences
    override fun getHistory(): List<Track> {
        try {
            val json = sharedPreferences.getString(HISTORY_KEY, "[]") ?: "[]"
            return fromJsonList(json)
        } catch (e: Exception) {
            return emptyList()  // Возвращаем пустой список в случае ошибки
        }
    }

    // Очистка истории
    override fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }
}
