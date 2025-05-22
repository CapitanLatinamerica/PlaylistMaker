package com.practicum.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.db.domain.model.FavoriteTrackEntity

@Database(
    entities = [FavoriteTrackEntity::class],        //список всех таблиц в БД
    version = 1,                                    //текущая версия БД
    exportSchema = false                            //схему не создаём
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao   //доступ к DAO
}
