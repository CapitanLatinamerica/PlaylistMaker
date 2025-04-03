package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : SearchHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
    }

    // Функция для сериализации списка объектов в JSON
    private fun <T> toJsonList(list: List<T>): String {
        return Json.encodeToString(list)
    }

    // Функция для десериализации списка объектов из JSON
    private fun <T> fromJsonList(json: String): List<T> {
        return Json.decodeFromString(json)
    }

    // Сохранение списка треков в SharedPreferences
    override fun saveTrack(track: Track) {
        val history = getHistory().toMutableList()
        if (history.size >= 10) {
            history.removeAt(0) // Убираем самый старый элемент, если их больше 10
        }
        history.add(track)
        sharedPreferences.edit().putString(HISTORY_KEY, toJsonList(history)).apply()
    }

    // Получение списка треков из SharedPreferences
    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, "[]") ?: "[]"
        return fromJsonList(json)
    }

    // Очистка истории
    override fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }
}
