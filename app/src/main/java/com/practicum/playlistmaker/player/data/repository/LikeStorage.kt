package com.practicum.playlistmaker.player.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.db.data.FavoriteTrackDao

class LikeStorage(
    context: Context,
    private val favoriteTrackDao: FavoriteTrackDao // добавляем зависимость от DAO
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("likes", Context.MODE_PRIVATE)

    fun isLiked(trackId: Long): Boolean {
        val isLiked = sharedPreferences.getBoolean("track_$trackId", false)
        return isLiked
    }

    //Здесь происходит переключение кнопки
    fun toggleLike(trackId: Long): Boolean {
        val isCurrentlyLiked = isLiked(trackId)
        val newState = !isCurrentlyLiked
        val editor = sharedPreferences.edit()
        editor.putBoolean("track_$trackId", newState)
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

    fun getTrackLocalId(trackId: Long): Long?{
        return try {
            favoriteTrackDao.getTrackById(trackId)?.localId
        } catch (e: Exception) {
            null
        }
    }
}