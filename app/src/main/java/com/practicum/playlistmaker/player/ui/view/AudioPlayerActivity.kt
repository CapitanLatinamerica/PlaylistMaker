package com.practicum.playlistmaker.player.ui.view

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.practicum.playlistmaker.player.ui.viewmodel.PlayerViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.player.data.Constants
import com.practicum.playlistmaker.player.data.repository.LikeStorage
import com.practicum.playlistmaker.player.domain.model.PlayerState

class AudioPlayerActivity : AppCompatActivity() {

    // Binding для работы с layout
    private lateinit var binding: ActivityAudioplayerBinding
    private val likeStorage: LikeStorage by lazy { LikeStorage(applicationContext) } // Инициализируем в самом Activity

    // ViewModel для управления логикой плеера
    // Получаем ViewModel с передачей LikeStorage
    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(
            PlayerRepositoryImpl(MediaPlayer()),
            likeStorage
        )
    }

    // Текущий трек
    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация ViewBinding
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка toolbar (кнопка "назад")
        setupToolbar()

        // Создание трека из Intent
        track = createTrackFromIntent()
        viewModel.setTrack(track)

        // Проверка и инициализация UI
        track?.let {
            setupUI(it)
            viewModel.setInitialDuration() // Установка начальной длительности (30 сек)
            setupObservers() // Настройка наблюдателей
            setupListeners() // Настройка слушателей
            it.isFavorite = likeStorage.isLiked(it.trackId)
        } ?: run {
            // Обработка ошибки загрузки трека
            Toast.makeText(this, "Ошибка загрузки трека", Toast.LENGTH_LONG).show()
            finish()
        }

        // Получение данных о треке из Intent
        val trackName = intent.getStringExtra(Constants.Extra.TRACK_NAME) ?: "Unknown"
        val artistName = intent.getStringExtra(Constants.Extra.ARTIST_NAME) ?: "Unknown"
        val trackTimeMillis = intent.getStringExtra(Constants.Extra.TRACK_TIME)?.toLongOrNull() ?: 0
        val albumCover = intent.getStringExtra(Constants.Extra.ALBUM_COVER)
        val collectionName = intent.getStringExtra(Constants.Extra.COLLECTION_NAME)
        val releaseYear = intent.getStringExtra(Constants.Extra.RELEASE_YEAR)
        val genre = intent.getStringExtra(Constants.Extra.GENRE)
        val country = intent.getStringExtra(Constants.Extra.COUNTRY)

        // Заполнение UI данными о треке
        findViewById<TextView>(R.id.track_name).text = trackName
        findViewById<TextView>(R.id.artist_name).text = artistName
        findViewById<TextView>(R.id.current_time).text = "00:30" // Хардкод 30 секунд

        // Загрузка обложки с помощью Glide
        Glide.with(this)
            .load(albumCover)
            .into(findViewById(R.id.track_cover))

        // Настройка дополнительной информации о треке
        val trackInfoContainer = findViewById<LinearLayout>(R.id.track_info_container)
        trackInfoContainer.removeAllViews()

        // Список параметров для отображения
        val params = listOfNotNull(
            "Длительность" to formatTime(trackTimeMillis),
            "Альбом" to collectionName,
            "Год" to releaseYear,
            "Жанр" to genre,
            "Страна" to country
        )

        // Добавление параметров в UI
        params.forEach { (param, value) ->
            if (!value.isNullOrEmpty()) {
                val row = LayoutInflater.from(this).inflate(R.layout.track_info_row, trackInfoContainer, false)
                row.findViewById<TextView>(R.id.parameter_name).text = param
                row.findViewById<TextView>(R.id.parameter_value).text = value
                trackInfoContainer.addView(row)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
    //Настройка основных элементов UI @param track Трек для отображения
    private fun setupUI(track: Track) {
        binding.trackName.setText(track.trackName)
        binding.artistName.setText(track.artistName)
        // Загрузка обложки с высоким разрешением
        Glide.with(this)
            .load(track.getArtworkUrl512())
            .into(binding.trackCover)
    }

    //Настройка наблюдателей за состоянием плеера
    private fun setupObservers() {
        // Наблюдатель за состоянием плеера
        lifecycleScope.launch {
            viewModel.playerState.collect { state ->
                when (state) {
                    PlayerState.PLAYING ->
                        binding.buttonPlay.setImageResource(R.drawable.ic_pause)

                    PlayerState.PAUSED ->
                        binding.buttonPlay.setImageResource(R.drawable.ic_play)

                    PlayerState.IDLE ->
                        binding.buttonPlay.setImageResource(R.drawable.ic_play)

                    is PlayerState.ERROR ->
                        Toast.makeText(
                            this@AudioPlayerActivity,
                            "Ошибка: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    else -> {}
                }
            }
        }

        //Слушаем кнопку лайк
        lifecycleScope.launch {
            viewModel.isLiked.collect { isLiked ->
                val icon = if (isLiked) R.drawable.ic_liked_song else R.drawable.ic_unliked_song
                binding.buttonLike.setImageResource(icon)
            }
        }

        // Наблюдатель за прогрессом воспроизведения
        lifecycleScope.launch {
            viewModel.progress.collectLatest { remainingTimeMs ->
                binding.currentTime.text = formatTime(remainingTimeMs.toLong())
            }
        }
    }

    //Настройка слушателей кликов
    private fun setupListeners() {
        binding.buttonPlay.setOnClickListener {
            viewModel.playerState.value?.let { state ->
                when (state) {
                    PlayerState.IDLE -> {
                        track?.let {
                            viewModel.playTrack(it)
                        }
                    }
                    PlayerState.PLAYING -> {
                        viewModel.togglePlayPause()
                    }
                    PlayerState.PAUSED -> {
                        viewModel.togglePlayPause()
                    }
                    PlayerState.PREPARING -> {
                        // Ничего не делаем во время подготовки
                    }
                    else -> {}
                }
            }
        }
        //Пользователь нажимает кнопку Лайк! Идём во ViewModel и там обработаем
        binding.buttonLike.setOnClickListener {
            viewModel.onLikeClicked()
        }
    }

    // Форматирование времени из миллисекунд в строку MM:SS
    private fun formatTime(millis: Long): String {
        val minutes = millis / 60000
        val seconds = (millis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    //Создание объекта Track из данных Intent
    private fun createTrackFromIntent(): Track? {
        return try {
            Track(
                trackId = intent.getLongExtra(Constants.Extra.TRACK_ID, -1),
                trackName = intent.getStringExtra(Constants.Extra.TRACK_NAME) ?: return null,
                artistName = intent.getStringExtra(Constants.Extra.ARTIST_NAME) ?: return null,
                trackTimeMillis = intent.getStringExtra(Constants.Extra.TRACK_TIME)?.toLongOrNull() ?: 0,
                artworkUrl100 = intent.getStringExtra(Constants.Extra.ALBUM_COVER)
                    ?.replace("512x512bb.jpg", "100x100bb.jpg") ?: "",
                previewUrl = intent.getStringExtra(Constants.Extra.PREVIEW_URL) ?: return null
            )
        } catch (e: Exception) {
            null
        }
    }
}