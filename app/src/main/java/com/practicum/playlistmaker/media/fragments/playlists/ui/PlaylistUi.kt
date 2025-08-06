package com.practicum.playlistmaker.media.fragments.playlists.ui

data class PlaylistUi(
    val id: Int,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackCount: Int
)
