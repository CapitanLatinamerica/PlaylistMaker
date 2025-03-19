package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.domain.Track
import android.content.SharedPreferences
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Класс для управления историей поиска треков. История сохраняется в SharedPreferences в формате JSON.
class SearchHistory(private val preferences: SharedPreferences) {

    companion object {
        private const val HISTORY_KEY = "search_history"                                            // Ключ для хранения истории поиска в SharedPreferences
        private const val MAX_HISTORY_SIZE = 10                                                     // Максимальное количество треков в истории
    }

    //Получает историю поиска из SharedPreferences. return Список треков, сохранённых в истории
    fun getHistory(): List<Track> {
        val json = preferences.getString(HISTORY_KEY, null) ?: return emptyList()                   // Получаем JSON-строку из SharedPreferences
        return try {                                                                                // Пытаемся декодировать JSON-строку в список треков
            Json.decodeFromString(json)

        } catch (e: Exception) {                                                                    // В случае ошибки возвращаем пустой список
            emptyList()
        }
    }

    //Добавляет трек в историю поиска. Если трек уже есть в истории, он будет перемещён в начало. track Трек, который нужно добавить в историю
    fun addTrack(track: Track) {

        val history = getHistory().toMutableList()                                                  // Получаем текущую историю поиска
        history.removeAll { it.trackId == track.trackId }                                           // Удаляем дубликаты (если трек уже есть в истории)
        history.add(0, track)                                                                 // Добавляем трек в начало списка
        if (history.size > MAX_HISTORY_SIZE) {                                                      // Ограничиваем размер истории (удаляем последний элемент, если история превышает лимит)
            history.removeLast()
        }

        saveHistory(history)                                                                        // Сохраняем обновлённую историю
    }

    fun clearHistory() {                                                                            // Очищает историю поиска
        preferences.edit().remove(HISTORY_KEY).apply()                                              // Удаляем данные по ключу HISTORY_KEY из SharedPreferences
    }

    //Сохраняет историю поиска в SharedPreferences в формате JSON.  history Список треков для сохранения.
    private fun saveHistory(history: List<Track>) {
        val json = Json.encodeToString(history)                                                     // Кодируем список треков в JSON-строку
        preferences.edit()                                                                          // Сохраняем JSON-строку в SharedPreferences
            .putString(HISTORY_KEY, json)
            .apply()
    }
}