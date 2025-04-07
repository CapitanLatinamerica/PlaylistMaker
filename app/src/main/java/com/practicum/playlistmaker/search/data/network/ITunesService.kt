package com.practicum.playlistmaker.search.data.network

import retrofit2.http.GET
import retrofit2.http.Query

// Интерфейс для работы с API iTunes
interface ITunesService {

    // Поиск песен по запросу
    @GET("search")
    suspend fun searchSongs(
        @Query("term") text: String,                                                                // Поисковый запрос
        @Query("entity") entity: String = ENTITY_SONG                                               // Тип сущности (по умолчанию - песни)
    ): ITunesSearchResponse

    companion object {
        const val ENTITY_SONG = "song"                                                              // Константа для типа сущности

        // Создание экземпляра ITunesService
        fun create(): ITunesService {
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")                                               // Базовый URL
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())        // Конвертер Gson
                .build()

            return retrofit.create(ITunesService::class.java)
        }
    }
}