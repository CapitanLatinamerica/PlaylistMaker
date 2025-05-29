package com.practicum.playlistmaker.player.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.practicum.playlistmaker.player.domain.Track

class LikeStorage(context: Context) {
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

    // Метод для добавления трека в избранное
    fun addToFavorites(track: Track) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("track_${track.trackId}", true) // Помечаем как избранное
        editor.apply()
    }

    // Метод для удаления трека из избранного
    fun removeFromFavorites(track: Track) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("track_${track.trackId}", false) // Убираем отметку избранного
        editor.apply()
    }
}