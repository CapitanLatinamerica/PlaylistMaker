package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import android.util.Log
import com.practicum.playlistmaker.player.data.repository.LikeStorage
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val likeStorage: LikeStorage
) : SearchHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val TAG = "MyAwesomeSearchHistoryRepo" // Тег для логов
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
        if (history.size >= 10) { history.removeAt(history.size - 1) } // Убираем самый старый элемент, если их больше 10

        history.removeAll { existingTrack -> existingTrack.trackId == track.trackId }
        history.add(0, track)                                                             // Добавляем трек в начало списка

        val historyWithAddedAt = history.map { updatedTrack ->
            if ((updatedTrack.isFavorite)&&(updatedTrack.trackId == track.trackId)) {
                updatedTrack.copy(addedAt = track.addedAt)
            } else {
                updatedTrack // Для треков, не добавленных в избранное, не меняем addedAt
            }
        }

        val sortedHistory = historyWithAddedAt
            .sortedWith(compareByDescending<Track> { likeStorage.trackExists(it.trackId) }
                .thenByDescending { it.addedAt })
        sharedPreferences.edit().putString(HISTORY_KEY, toJsonList(sortedHistory)).apply()            // Сохраняем историю в SharedPreferences
    }

    override fun getHistory(): List<Track> {
        return try {
            val json = sharedPreferences.getString(HISTORY_KEY, "[]") ?: "[]"
            val history = fromJsonList(json)

            val likedTrackIds = likeStorage.getAllLikedTrackIds()
            val updatedHistory = history.map { track ->
                track.copy(isFavorite = likedTrackIds.contains(track.trackId), addedAt = if (likedTrackIds.contains(track.trackId)) track.addedAt else 0L)
            }

            updatedHistory
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Очистка истории
    override fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }
}
