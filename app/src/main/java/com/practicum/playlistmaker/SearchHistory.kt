package com.practicum.playlistmaker

import Track
import android.content.SharedPreferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SearchHistory(private val preferences: SharedPreferences) {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun getHistory(): List<Track> {
        val json = preferences.getString(HISTORY_KEY, null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        // Удаляем дубликаты
        history.removeAll { it.trackId == track.trackId }

        // Добавляем трек в начало
        history.add(0, track)

        // Ограничиваем размер истории
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeLast()
        }

        saveHistory(history)
    }

    fun clearHistory() {
        preferences.edit().remove(HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = Json.encodeToString(history)
        preferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }
}
