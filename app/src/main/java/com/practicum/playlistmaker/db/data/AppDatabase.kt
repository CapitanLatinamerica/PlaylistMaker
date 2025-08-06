package com.practicum.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.db.data.favorites.FavoriteTrackDao
import com.practicum.playlistmaker.db.data.favorites.FavoriteTrackEntity
import com.practicum.playlistmaker.db.data.playlists.PlaylistEntity
import com.practicum.playlistmaker.db.data.playlists.PlaylistDao

@Database(
    entities = [FavoriteTrackEntity::class, PlaylistEntity::class],        //список всех таблиц в БД
    version = 2,                                    //текущая версия БД
    exportSchema = false                            //схему не создаём
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao   //доступ к DAO
    abstract fun playlistDao(): PlaylistDao
}


