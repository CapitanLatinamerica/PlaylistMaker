package com.practicum.playlistmaker.player.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class LikeStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("likes", Context.MODE_PRIVATE)

    fun isLiked(trackId: Long): Boolean {
        val isLiked = sharedPreferences.getBoolean("track_$trackId", false)
        Log.d("MyAwesomeLikeButton", "isLiked called for trackId: $trackId, result: $isLiked")
        return isLiked
    }

    //Здесь происходит переключение кнопки
    fun toggleLike(trackId: Long): Boolean {
        val isCurrentlyLiked = isLiked(trackId)
        val newState = !isCurrentlyLiked
        val editor = sharedPreferences.edit()
        editor.putBoolean("track_$trackId", newState)
        editor.apply()


        Log.d("MyAwesomeLikeButton", "toggleLike called for trackId: $trackId, newState: $newState")
        return newState
    }
}

