package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.network.ITunesSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepositoryImpl(private val iTunesService: ITunesService) : SearchRepository {

    override fun searchTracks(query: String, callback: (List<Track>) -> Unit) {
        val call = iTunesService.searchSongs(query) // Используем метод searchSongs, который возвращает ITunesSearchResponse
        call.enqueue(object : Callback<ITunesSearchResponse> {  // Исправили тип на ITunesSearchResponse
            override fun onResponse(
                call: Call<ITunesSearchResponse>,
                response: Response<ITunesSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.results?.map { it.toDomain() } ?: emptyList()
                    callback(tracks)
                } else {
                    callback(emptyList())
                }
            }

            // Исправили тип в onFailure на ITunesSearchResponse
            override fun onFailure(call: Call<ITunesSearchResponse>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
}
