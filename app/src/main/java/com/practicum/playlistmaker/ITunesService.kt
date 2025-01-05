package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesService {
    @GET("search")
    fun searchSongs(
        @Query("term") text: String,
        @Query("entity") entity: String = "song"
    ): Call<ITunesSearchResponse>
}


data class ITunesSearchResponse(
    val resultCount: Int,
    val results: List<Track>
)



