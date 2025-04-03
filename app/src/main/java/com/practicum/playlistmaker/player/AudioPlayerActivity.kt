package com.practicum.playlistmaker.player

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.practicum.playlistmaker.R

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false
    private var currentTrackPosition = 0  // Сохранение текущей позиции

    private lateinit var currentTimeTextView: TextView
    private lateinit var playButton: ImageButton

    private val handler = Handler(Looper.getMainLooper())  // Обработчик для обновления UI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        mediaPlayer = MediaPlayer()
        sharedPreferences = getSharedPreferences("audio_player_prefs", Context.MODE_PRIVATE)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val albumCoverImageView: ImageView = findViewById(R.id.track_cover)
        val trackNameTextView: TextView = findViewById(R.id.track_name)
        val artistNameTextView: TextView = findViewById(R.id.artist_name)
        val trackInfoContainer: LinearLayout = findViewById(R.id.track_info_container)

        currentTimeTextView = findViewById(R.id.current_time)  // Инициализируем TextView для времени
        playButton = findViewById(R.id.buttonPlay)

        toolbar.setNavigationOnClickListener { finish() }

        // Получаем данные из Intent
        val intent = intent
        val trackName = intent.getStringExtra("track_name")
        val collectionName = intent.getStringExtra("collection_name")
        val releaseYear = intent.getStringExtra("release_year")
        val primaryGenreName = intent.getStringExtra("genre")
        val country = intent.getStringExtra("country")
        val previewUrl = intent.getStringExtra("preview_url")

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

        // Обработка кнопки Play
        playButton.setOnClickListener {
            Log.d("AudioPlayer", "Play button clicked. isPlaying: $isPlaying, currentTrackPosition: $currentTrackPosition")

            if (isPlaying) {
                Log.d("AudioPlayer", "Pausing the track")
                pauseTrack()  // Приостанавливаем воспроизведение
            } else {
                // Если трек был приостановлен, продолжаем с текущей позиции
                if (currentTrackPosition > 0) {
                    Log.d("AudioPlayer", "Resuming track from position: $currentTrackPosition")
                    mediaPlayer.seekTo(currentTrackPosition)  // Восстанавливаем позицию
                    mediaPlayer.start()  // Продолжаем воспроизведение
                    isPlaying = true
                    findViewById<ImageButton>(R.id.buttonPlay).setImageResource(R.drawable.ic_pause)  // Меняем иконку на "Pause"
                    startUpdatingTime()  // Начинаем обновление времени
                } else {
                    // Если трек ещё не был проигран, начинаем его с начала
                    Log.d("AudioPlayer", "Starting new track from the beginning")
                    previewUrl?.let { url -> startTrack(url) }
                }
            }
        }


        // Динамически добавляем параметры в trackInfoContainer
        val parameters = listOfNotNull(
            "Длительность" to trackTimeFormatted.takeIf { it.isNotEmpty() },
            "Альбом" to collectionName.takeIf { !collectionName.isNullOrEmpty() },
            "Год" to releaseYear.takeIf { !releaseYear.isNullOrEmpty() },
            "Жанр" to (intent.getStringExtra("genre") ?: primaryGenreName).takeIf { !it.isNullOrEmpty() },
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

        // Настроим кнопку "Like"
        val likeButton: ImageButton = findViewById(R.id.buttonLike)
        likeButton.setOnClickListener {
            val isSelected = likeButton.isSelected
            likeButton.isSelected = !isSelected

            if (likeButton.isSelected) {
                Toast.makeText(this, "Liked!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unliked!", Toast.LENGTH_SHORT).show()
            }
        }

        // Настроим кнопку "Добавить в плейлист"
        val addToPlaylistButton: ImageButton = findViewById(R.id.buttonAdd)
        addToPlaylistButton.setOnClickListener {
            val isSelected = addToPlaylistButton.isSelected
            addToPlaylistButton.isSelected = !isSelected

            if (addToPlaylistButton.isSelected) {
                Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {                                                                        // Приостанавливаем медиаплеер при уходе в фоновый режим
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            pauseTrack()                                                                            // Автоматически ставим трек на паузу при сворачивании приложения
            currentTrackPosition = mediaPlayer.currentPosition                                      // Сохраняем текущую позицию
        }
    }

    override fun onResume() {                                                                       // Восстанавливаем воспроизведение при возвращении в активное состояние
        super.onResume()
        if (currentTrackPosition > 0) {
            mediaPlayer.seekTo(currentTrackPosition)                                                // Восстанавливаем позицию
            if (isPlaying) {
                mediaPlayer.start()                                                                 // Продолжаем воспроизведение
            }
        }
    }

    private fun startTrack(url: String) {
        try {
            mediaPlayer.reset()                                                                     // Сбрасываем текущий плеер
            mediaPlayer.setDataSource(url)                                                          // Устанавливаем источник
            mediaPlayer.prepareAsync()                                                              // Асинхронная подготовка плеера

            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()                                                                 // Стартуем плеер
                isPlaying = true
                findViewById<ImageButton>(R.id.buttonPlay).setImageResource(R.drawable.ic_pause)    // Меняем иконку на "Pause"
                startUpdatingTime()                                                                 // Начинаем обновление времени
            }

            mediaPlayer.setOnCompletionListener {
                stopTrack()                                                                         // Останавливаем трек по завершению
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error playing track: $e")
        }
    }

    private fun pauseTrack() {
        mediaPlayer.pause()
        isPlaying = false
        currentTrackPosition = mediaPlayer.currentPosition
        findViewById<ImageButton>(R.id.buttonPlay).setImageResource(R.drawable.ic_play)             // Меняем иконку на "Play"
        handler.removeCallbacks(updateTimeRunnable)                                                 // Останавливаем обновление времени
    }

    private fun stopTrack() {
        mediaPlayer.stop()
        isPlaying = false
        findViewById<ImageButton>(R.id.buttonPlay).setImageResource(R.drawable.ic_play)             // Меняем иконку на "Play"
        handler.removeCallbacks(updateTimeRunnable)                                                 // Останавливаем обновление времени
    }

    private val updateTimeRunnable: Runnable = object : Runnable {                                  // Обновление времени
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val currentPosition = mediaPlayer.currentPosition
                val duration = mediaPlayer.duration                                                 // Берём продолжительность из медиаплеера
                val remainingTime = duration - currentPosition - 1000

                val minutes = remainingTime / 60000
                val seconds = (remainingTime % 60000) / 1000
                currentTimeTextView.text = String.format("%02d:%02d", minutes, seconds)

                handler.postDelayed(this, 1000)                                         // Запускаем обновление времени каждую секунду
            }
        }
    }

    private fun startUpdatingTime() {
        handler.post(updateTimeRunnable)                                                            // Начинаем обновление времени
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()                                                                       // Освобождаем ресурсы плеера
        handler.removeCallbacks(updateTimeRunnable)                                                 // Убираем все запланированные обновления времени
    }
}
