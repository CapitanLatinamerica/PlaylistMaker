package com.practicum.playlistmaker.player

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
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

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioplayerBinding
    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(PlayerRepositoryImpl(MediaPlayer()))
    }

    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("PLAYER_DEBUG", "Intent extras: ${intent.extras?.keySet()}")
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = createTrackFromIntent()

        // Потом настраиваем UI
        track?.let {
            setupUI(it)
            setupObservers()
            setupListeners()
        } ?: run {
            Toast.makeText(this, "Ошибка загрузки трека", Toast.LENGTH_LONG).show()
            finish()
        }

        // Достаём все переданные поля
        val trackName = intent.getStringExtra("track_name") ?: "Unknown"
        val artistName = intent.getStringExtra("artist_name") ?: "Unknown"
        val trackTimeMillis = intent.getStringExtra("track_time")?.toLongOrNull() ?: 0
        val artworkUrl512 = intent.getStringExtra("album_cover") ?: ""
        val previewUrl = intent.getStringExtra("preview_url")
        Log.d("PLAYER_DEBUG", "Received previewUrl: $previewUrl")
        val albumCover = intent.getStringExtra("album_cover")
        val collectionName = intent.getStringExtra("collection_name")
        val releaseYear = intent.getStringExtra("release_year")
        val genre = intent.getStringExtra("genre")
        val country = intent.getStringExtra("country")

        // Заполняем UI
        findViewById<TextView>(R.id.track_name).text = trackName
        findViewById<TextView>(R.id.artist_name).text = artistName
        findViewById<TextView>(R.id.current_time).text = formatTime(trackTimeMillis)

        // Загружаем обложку
        Glide.with(this)
            .load(albumCover)
            .into(findViewById(R.id.track_cover))

        // Добавляем параметры трека (альбом, год, жанр и т.д.)
        val trackInfoContainer = findViewById<LinearLayout>(R.id.track_info_container)
        trackInfoContainer.removeAllViews()

        val params = listOfNotNull(
            "Длительность" to formatTime(trackTimeMillis),
            "Альбом" to collectionName,
            "Год" to releaseYear,
            "Жанр" to genre,
            "Страна" to country
        )

        params.forEach { (param, value) ->
            if (!value.isNullOrEmpty()) {
                val row = LayoutInflater.from(this).inflate(R.layout.track_info_row, trackInfoContainer, false)
                row.findViewById<TextView>(R.id.parameter_name).text = param
                row.findViewById<TextView>(R.id.parameter_value).text = value
                trackInfoContainer.addView(row)
            }
        }

        val track = Track(
            trackId = -1,  // Временный ID
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl512.replace("512x512bb.jpg", "100x100bb.jpg"), // Обратная замена
            previewUrl = previewUrl
        ).also {
            Log.d("TRACK_INIT", "Track created: ${it.trackName}, URL: ${it.previewUrl}")
        }

        setupUI(track)
        setupObservers()
        setupListeners()
    }

    private fun setupUI(track: Track) {
        binding.trackName.setText(track.trackName)
        binding.artistName.setText(track.artistName)
        Glide.with(this)
            .load(track.getArtworkUrl512())
            .into(binding.trackCover)
    }

    private fun setupObservers() {
        // LiveData
        viewModel.playerState.observe(this) { state ->
            Log.d("PLAYER_DEBUG", "Player state changed: $state")
            when (state) {
                is PlayerViewModel.PlayerState.PLAYING ->
                    binding.buttonPlay.setImageResource(R.drawable.ic_pause)
                is PlayerViewModel.PlayerState.PAUSED ->
                    binding.buttonPlay.setImageResource(R.drawable.ic_play)
                is PlayerViewModel.PlayerState.ERROR ->
                    Toast.makeText(this, "Ошибка: ${state.message}", Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }

        // StateFlow (коллектим в корутине)
        lifecycleScope.launch {
            viewModel.progress.collectLatest { position ->
                val minutes = position / 60000
                val seconds = (position % 60000) / 1000
                binding.currentTime.setText(String.format("%02d:%02d", minutes, seconds))
            }
        }
    }

    private fun setupListeners() {
        binding.buttonPlay.setOnClickListener {
            viewModel.playerState.value?.let { state ->
                when (state) {
                    PlayerViewModel.PlayerState.IDLE -> track?.let(viewModel::playTrack)
                    PlayerViewModel.PlayerState.PLAYING -> viewModel.togglePlayPause()
                    PlayerViewModel.PlayerState.PAUSED -> viewModel.togglePlayPause()
                    else -> {}
                }
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val minutes = millis / 60000
        val seconds = (millis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun createTrackFromIntent(): Track? {

        return try {
            Track(
                trackId = -1,
                trackName = intent.getStringExtra("track_name") ?: return null,
                artistName = intent.getStringExtra("artist_name") ?: return null,
                trackTimeMillis = intent.getStringExtra("track_time")?.toLongOrNull() ?: 0,
                artworkUrl100 = intent.getStringExtra("album_cover")?.replace("512x512bb.jpg", "100x100bb.jpg") ?: "",
                previewUrl = intent.getStringExtra("preview_url") ?: return null
            )
        } catch (e: Exception) {
            Log.e("TRACK_CREATION", "Error creating track", e)
            null
        }
    }
}