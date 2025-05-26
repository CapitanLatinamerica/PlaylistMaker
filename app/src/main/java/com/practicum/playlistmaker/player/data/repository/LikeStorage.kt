package com.practicum.playlistmaker.player.data.repository

import android.content.Context
import android.content.SharedPreferences

class LikeStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("likes", Context.MODE_PRIVATE)

    fun isLiked(trackId: Long): Boolean {
        return sharedPreferences.getBoolean("track_$trackId", false)
    }

    fun toggleLike(trackId: Long): Boolean {
        val isCurrentlyLiked = isLiked(trackId)
        val editor = sharedPreferences.edit()
        editor.putBoolean("track_$trackId", !isCurrentlyLiked)
        editor.apply()
        return !isCurrentlyLiked
    }
}
