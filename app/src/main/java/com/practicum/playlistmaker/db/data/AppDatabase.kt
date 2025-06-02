package com.practicum.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [FavoriteTrackEntity::class],        //список всех таблиц в БД
    version = 2,                                    //текущая версия БД
    exportSchema = false                            //схему не создаём
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao   //доступ к DAO
}
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. Создаём новую таблицу с нужной структурой
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS favorite_tracks_new (
                localId INTEGER PRIMARY KEY AUTOINCREMENT,
                trackId INTEGER NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                collectionName TEXT,
                releaseDate TEXT NOT NULL,
                genre TEXT NOT NULL,
                country TEXT NOT NULL,
                previewUrl TEXT NOT NULL
            )
        """.trimIndent())

        // 2. Копируем данные, обрабатывая возможные NULL
        database.execSQL("""
            INSERT INTO favorite_tracks_new (
                trackId, trackName, artistName, artworkUrl100,
                collectionName, releaseDate, genre, country, previewUrl
            )
            SELECT 
                trackId, 
                trackName, 
                artistName, 
                artworkUrl100,
                collectionName,
                IFNULL(releaseDate, '') AS releaseDate,
                '' AS genre,
                '' AS country,
                '' AS previewUrl
            FROM favorite_tracks
        """.trimIndent())

        // 3. Удаляем старую таблицу
        database.execSQL("DROP TABLE favorite_tracks")

        // 4. Переименовываем новую
        database.execSQL("ALTER TABLE favorite_tracks_new RENAME TO favorite_tracks")
    }
}

