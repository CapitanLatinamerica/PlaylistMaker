package com.practicum.playlistmaker.player.data.repository

import android.content.Context
import android.content.SharedPreferences

class LikeStorage(
    context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("likes", Context.MODE_PRIVATE)

    fun isLiked(trackId: Long): Boolean {
        val isLiked = sharedPreferences.getBoolean("track_$trackId", false)
        return isLiked
    }

    fun addedAt(trackId: Long): Long {
        val addedAt = sharedPreferences.getLong("track_$trackId", 0L)
        return addedAt
    }

    //Здесь происходит переключение кнопки
    fun toggleLike(trackId: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val isCurrentlyLiked = isLiked(trackId)
        val newState = !isCurrentlyLiked

        val editor = sharedPreferences.edit()
        editor.putBoolean("track_$trackId", newState)

        // Устанавливаем время добавления только если трек был добавлен в избранное
        if (newState) {
            editor.putLong("track_${trackId}_addedAt", currentTime)
        } else {
            // Сброс времени, если трек удален из избранного
            editor.putLong("track_${trackId}_addedAt", 0L)
        }
        editor.apply()
        return newState
    }

    fun trackExists(trackId: Long): Boolean {
        return sharedPreferences.contains("track_$trackId") && isLiked(trackId)
    }

    fun getAllLikedTrackIds(): Set<Long> {
        return sharedPreferences.all
            .filter { it.key.startsWith("track_") && it.value == true }
            .mapNotNull { it.key.removePrefix("track_").toLongOrNull() }
            .toSet()
    }
}