package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import android.util.Log
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : SearchHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
    }

    // Функция для сериализации списка объектов в JSON
    private fun toJsonList(tracks: List<Track>): String {
        return Json.encodeToString(tracks) // Кодируем список в JSON
    }

    // Функция для десериализации списка объектов из JSON
    fun fromJsonList(json: String?): List<Track> {
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                // Используем kotlinx.serialization для десериализации
                Json.decodeFromString<List<Track>>(json)

            } catch (e: Exception) {
                emptyList()  // Возвращаем пустой список в случае ошибки
            }
        }
        Log.d("SearchHistoryRepositoryImpl", "Loaded JSON from SharedPreferences: $json")
    }


    // Сохранение трека в историю
    override fun saveTrack(track: Track) {
        try {
            val history = getHistory().toMutableList()
            if (history.size >= 10) {
                history.removeAt(0) // Убираем самый старый элемент, если их больше 10
            }
            history.add(0, track) // Добавляем трек в начало списка

            // Логирование перед сохранением
            Log.d("SearchHistoryRepository", "Saving track to history: ${track.trackName}")

            sharedPreferences.edit().putString(HISTORY_KEY, toJsonList(history)).apply()  // Сохраняем историю в SharedPreferences

            // Логирование успешного сохранения
            Log.d("SearchHistoryRepository", "Track saved successfully.")
        } catch (e: Exception) {
            // Логируем ошибку
            Log.e("SearchHistoryRepository", "Error saving track to history: ${e.message}", e)
        }
    }

    // Получение списка треков из SharedPreferences
    override fun getHistory(): List<Track> {
        try {
            val json = sharedPreferences.getString(HISTORY_KEY, "[]") ?: "[]"
            // Логирование истории, полученной из SharedPreferences
            Log.d("SearchHistoryRepository", "Loaded history: $json")
            return fromJsonList(json)
        } catch (e: Exception) {
            Log.e("SearchHistoryRepository", "Error getting history: ${e.message}", e)
            return emptyList()  // Возвращаем пустой список в случае ошибки
        }
    }

    // Очистка истории
    override fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }
}
