package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import android.util.Log
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SearchHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val TAG = "SearchHistoryRepo" // Тег для логов
    }

    private val jsonFormat = Json {
        ignoreUnknownKeys = true // Игнорировать неизвестные ключи
        coerceInputValues = true // Принудительно преобразовывать некорректные значения
    }

    private fun toJsonList(tracks: List<Track>): String {
        return jsonFormat.encodeToString(tracks)
    }

    fun fromJsonList(json: String?): List<Track> {
        return try {
            jsonFormat.decodeFromString(json ?: "")
        } catch (e: Exception) {
            Log.e(TAG, "Ой, котик уронил JSON: ${e.message}")
            emptyList()
        }
    }

    // Сохранение трека в историю
    override fun saveTrack(track: Track) {
            val history = getHistory().toMutableList()
            if (history.size >= 10) {
                history.removeAt(0)                                                           // Убираем самый старый элемент, если их больше 10
            }
            history.removeAll { existingTrack -> existingTrack.trackId == track.trackId }
            history.add(0, track)                                                             // Добавляем трек в начало списка

            sharedPreferences.edit().putString(HISTORY_KEY, toJsonList(history)).apply()            // Сохраняем историю в SharedPreferences
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
