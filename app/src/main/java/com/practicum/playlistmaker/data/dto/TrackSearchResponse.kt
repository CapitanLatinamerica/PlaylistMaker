package com.practicum.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

data class TrackSearchResponse(
    @SerializedName("results") val results: List<TrackDto>
)
