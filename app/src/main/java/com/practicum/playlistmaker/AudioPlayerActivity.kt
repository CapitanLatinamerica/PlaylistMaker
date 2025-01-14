package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        sharedPreferences = getSharedPreferences("audio_player_prefs", Context.MODE_PRIVATE)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val albumCoverImageView: ImageView = findViewById(R.id.track_cover)
        val trackNameTextView: TextView = findViewById(R.id.track_name)
        val artistNameTextView: TextView = findViewById(R.id.artist_name)
        val trackInfoContainer: LinearLayout = findViewById(R.id.track_info_container)

        toolbar.setNavigationOnClickListener { finish() }

        // Получение данных из Intent
        val intent = intent
        val trackName = intent.getStringExtra("track_name")
        val collectionName = intent.getStringExtra("collection_name")
        val releaseYear = intent.getStringExtra("release_year")
        val genre = intent.getStringExtra("genre")
        val country = intent.getStringExtra("country")

        val artistName = intent.getStringExtra("artist_name")
        val trackTimeMillis = intent.getStringExtra("track_time")
        val albumCover = intent.getStringExtra("album_cover")
        val trackTimeFormatted = trackTimeMillis?.toLongOrNull()?.let {
            val minutes = it / 60000
            val seconds = (it % 60000) / 1000
            String.format("%02d:%02d", minutes, seconds)
        } ?: getString(R.string.unknown_duration)

        // Заполняем название трека и имя исполнителя
        trackNameTextView.text = trackName ?: getString(R.string.unknown_track)
        artistNameTextView.text = artistName ?: getString(R.string.unknown_artist)

        // Заполняем обложку
        Glide.with(this)
            .load(albumCover)
            .placeholder(R.drawable.my_awesome_placeholder)
            .into(albumCoverImageView)


        // Динамически добавляем параметры в trackInfoContainer
        val parameters = listOfNotNull(
            "Длительность" to trackTimeFormatted.takeIf { it.isNotEmpty() },
            "Альбом" to collectionName.takeIf { !collectionName.isNullOrEmpty() },
            "Год" to releaseYear.takeIf { !releaseYear.isNullOrEmpty() },
            "Жанр" to genre.takeIf { !genre.isNullOrEmpty() },
            "Страна" to country.takeIf { !country.isNullOrEmpty() }
        )

        // Очищаем контейнер перед добавлением новых элементов
        trackInfoContainer.removeAllViews()

        // Добавляем каждую пару параметров
        for ((param, value) in parameters) {
            if (!value.isNullOrEmpty()) {
                val rowView = LayoutInflater.from(this).inflate(R.layout.track_info_row, trackInfoContainer, false)
                rowView.findViewById<TextView>(R.id.parameter_name).text = param
                rowView.findViewById<TextView>(R.id.parameter_value).text = value
                trackInfoContainer.addView(rowView)
            }
        }


        val likeButton: ImageButton = findViewById(R.id.buttonLike)
        likeButton.setOnClickListener {
            val isSelected = likeButton.isSelected
            likeButton.isSelected = !isSelected

            if (likeButton.isSelected) {
                // Действие для "лайк"
                Toast.makeText(this, "Liked!", Toast.LENGTH_SHORT).show()
            } else {
                // Действие для "не лайк"
                Toast.makeText(this, "Unliked!", Toast.LENGTH_SHORT).show()
            }
        }

        val addToPlaylistButton: ImageButton = findViewById(R.id.buttonAdd)
        addToPlaylistButton.setOnClickListener {
            val isSelected = addToPlaylistButton.isSelected
            addToPlaylistButton.isSelected = !isSelected

            if (addToPlaylistButton.isSelected) {
                // Добавить в плейлист
                Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show()
            } else {
                // Удалить из плейлиста
                Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show()
            }
        } /*Это, наверное, надо будет удалить/переписать потом*/
    }

    override fun onPause() {
        super.onPause()
        saveState() // Сохраняем состояние при уходе в фон
    }

    private fun saveState() {
        val intent = intent
        sharedPreferences.edit()
            .putString("track_name", intent.getStringExtra("track_name"))
            .putString("artist_name", intent.getStringExtra("artist_name"))
            .putString("track_time", intent.getStringExtra("track_time"))
            .putString("album_cover", intent.getStringExtra("album_cover"))
            .putString("collection_name", intent.getStringExtra("collection_name"))
            .putString("release_year", intent.getStringExtra("release_year"))
            .putString("genre", intent.getStringExtra("genre"))
            .putString("country", intent.getStringExtra("country"))
            .apply()
    }
}
