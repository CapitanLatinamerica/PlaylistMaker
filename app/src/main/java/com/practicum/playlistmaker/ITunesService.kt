package com.practicum.playlistmaker

import Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesService {

    @GET("search")
    fun searchSongs(
        @Query("term") text: String,
        @Query("entity") entity: String = ENTITY_SONG
    ): Call<ITunesSearchResponse>

    companion object {
        const val ENTITY_SONG = "song"
    }
}

data class ITunesSearchResponse(
    val resultCount: Int,
    val results: List<Track>
)
