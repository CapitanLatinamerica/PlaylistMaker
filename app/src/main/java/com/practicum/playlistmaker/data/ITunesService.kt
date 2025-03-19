package com.practicum.playlistmaker.data  //Пакет для работы с данными (Data слой)

import com.practicum.playlistmaker.data.dto.TrackDto                                                // Импортируем TrackDto для работы с API
import retrofit2.Call                                                                               //Call — асинхронный запрос через Retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET                                                                           //GET-запрос
import retrofit2.http.Query                                                                         // Аннотация для передачи параметров в запросе

//Интерфейс для взаимодействия с API iTunes
interface ITunesService {

    @GET("search")                                                                                  //Определяем, что это GET-запрос по пути "search"
    fun searchSongs(
        @Query("term") text: String,                                                                //"term" — ключевое слово для поиска
        @Query("entity") entity: String = ENTITY_SONG                                               // "entity" — тип сущности (по умолчанию "song")
    ): Call<ITunesSearchResponse>  // Метод вернет Call с ITunesSearchResponse

    companion object {
        const val ENTITY_SONG = "song"                                                              // Константа для обозначения поиска песен
        // Метод для создания экземпляра ITunesService
        fun create(): ITunesService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")                                                   //Базовый URL
                .addConverterFactory(GsonConverterFactory.create())                                     //Используем Gson для конвертации
                .build()

            return retrofit.create(ITunesService::class.java)                                           //Возвращаем экземпляр ITunesService
        }
    }
}

//Ответ от сервера (список треков)
data class ITunesSearchResponse(
    val resultCount: Int,  //Количество найденных треков
    val results: List<TrackDto> //Список треков (в формате DTO)
)
