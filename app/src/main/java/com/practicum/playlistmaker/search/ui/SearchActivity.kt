package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.domain.interactors.GetSearchHistoryInteractor
import com.practicum.playlistmaker.domain.interactors.SaveSearchHistoryInteractor
import com.practicum.playlistmaker.domain.interactors.SearchTracksInteractor
import com.practicum.playlistmaker.player.AudioPlayerActivity
import com.practicum.playlistmaker.player.TrackAdapter
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    // UI элементы
    private lateinit var floatingContainer: ConstraintLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var errorImage: ImageView
    private lateinit var errorMessage: TextView
    private lateinit var retryButton: Button
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    // Интеракторы
    private lateinit var searchTracksInteractor: SearchTracksInteractor
    private lateinit var saveSearchHistoryInteractor: SaveSearchHistoryInteractor
    private lateinit var getSearchHistoryInteractor: GetSearchHistoryInteractor

    private val handler = Handler(Looper.getMainLooper()) // Handler для задержек
    private val debounceDelay: Long = 500 // Задержка перед поиском
    private val debounceRunnable = Runnable { performSearch(inputEditText.text.toString()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Получаем интеракторы из Creator
        searchTracksInteractor = Creator.provideSearchTracksInteractor()
        saveSearchHistoryInteractor = Creator.provideSaveSearchHistoryInteractor()
        getSearchHistoryInteractor = Creator.provideGetSearchHistoryInteractor()

        // Инициализация UI-компонентов
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        floatingContainer = findViewById(R.id.floating_container)
        inputEditText = findViewById(R.id.findEditText)
        clearIcon = findViewById(R.id.clearTextIcon)
        trackRecyclerView = findViewById(R.id.trackRecyclerView)
        errorImage = findViewById(R.id.error_image)
        errorMessage = findViewById(R.id.error_message)
        retryButton = findViewById(R.id.retry_button)
        historyTitle = findViewById(R.id.historyTitle)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progress_bar)

        trackAdapter = TrackAdapter(mutableListOf())
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter

        // Обработчик нажатия "Назад"
        toolbar.setNavigationOnClickListener { finish() }

        // Отобразить историю поиска
        showHistory()

        // Обработчик изменения фокуса у поля ввода
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Если поле в фокусе, скрываем историю, кнопку очистки и заголовок
                historyTitle.isVisible = false
                clearHistoryButton.isVisible = false
                trackAdapter.updateTracks(emptyList()) // Очищаем адаптер
            } else {
                // Если поле ввода не в фокусе и текст пустой, показываем историю
                if (inputEditText.text.isNullOrEmpty()) {
                    showHistory()
                }
            }
        }

        // Очистка поля ввода

        clearIcon.setOnClickListener {
            clearInputText()  // Очищаем текст в поле и обновляем интерфейс
            inputEditText.clearFocus()  // Убираем фокус у поля ввода
        }

        // Очистка истории
        clearHistoryButton.setOnClickListener { clearHistory() }

        // Обработчик изменения текста
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.isVisible = !s.isNullOrEmpty()
                handler.removeCallbacks(debounceRunnable)
                handler.postDelayed(debounceRunnable, debounceDelay)

                // Если в поле есть текст, скрываем заголовок истории
                if (!s.isNullOrEmpty()) {
                    historyTitle.isVisible = false
                    clearHistoryButton.isVisible = false // Скрываем кнопку очистки истории
                    hideError() // Скрываем все ошибки
                } else {
                    hideError()  // Скрываем все ошибки, если поле пустое
                    showHistory() // Показываем историю, если поле пустое
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // Обработчик клика по треку
        trackAdapter.setOnItemClickListener { track ->
            saveSearchHistory(track) // Сохраняем трек в историю
            openAudioPlayer(track) // Открываем плеер для выбранного трека
        }
    }

    // Функция для выполнения поиска
    private fun performSearch(query: String) {
        val sanitizedQuery = query.trim()                                                           // Убираем только лишние пробелы
        if (sanitizedQuery.isEmpty()) {
            showHistory()
            return
        }

        if (!isNetworkAvailable()) {
            showError(
                getString(R.string.no_internet_error),
                R.drawable.ic_no_connection,
                showRetryButton = true
            )
            return
        } else {
            hideError()
        }
        showLoading(true)

        lifecycleScope.launch {                                                                     //Для выполнения асинхронных операций
            try {
                val tracks = searchTracksInteractor.searchTracks(query)
                showLoading(false)

                // Обновляем адаптер данными
                if (tracks.isEmpty()) {
                    showError(
                        getString(R.string.nothing_founded),
                        R.drawable.ic_no_results,
                        showRetryButton = false
                    )
                } else {
                    trackAdapter.updateTracks(tracks)
                    hideError() // Скрываем ошибку, если результаты есть
                }
            } catch (e: Exception) {
                showLoading(false)
                showError(
                    getString(R.string.nothing_founded),
                    R.drawable.ic_no_results,
                    showRetryButton = false
                )
            }
        }
    }

    // Показываем историю поиска
    private fun showHistory() {
        lifecycleScope.launch {
            val history = getSearchHistoryInteractor.getSearchHistory()
            Log.d("SearchActivity", "History size: ${history.size}")  // Логируем размер истории
            Log.d("SearchActivity", "Is trackRecyclerView visible: ${trackRecyclerView.isVisible}")
            if (inputEditText.hasFocus() && !inputEditText.text.isNullOrEmpty()) {
                // Если поле ввода в фокусе и текст не пустой, скрываем историю, кнопку очистки и заголовок
                historyTitle.isVisible = false
                clearHistoryButton.isVisible = false
                trackAdapter.updateTracks(emptyList())
                Log.d("SearchActivity", "Hiding history: input field has focus and is not empty")
            } else {
                // Если фокуса нет или текст пустой, показываем историю
                historyTitle.isVisible = history.isNotEmpty()
                clearHistoryButton.isVisible = history.isNotEmpty()
                trackAdapter.updateTracks(history)
                trackRecyclerView.isVisible=true
                Log.d("SearchActivity", "Showing history: input field is empty or not focused")
            }
        }
    }

    // Функция для сохранения река в историю
    private fun saveSearchHistory(track: Track) {
        lifecycleScope.launch {
            saveSearchHistoryInteractor.saveSearchHistory(track)
        }
    }

    // Функция для очистки истории поиска
    private fun clearHistory() {
        lifecycleScope.launch {
            saveSearchHistoryInteractor.clearSearchHistory() // Очищаем историю
            showHistory() // Обновляем список
        }
    }

    // Функция для открытия плеера для выбранного трека
    private fun openAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra("track_name", track.trackName)
            putExtra("artist_name", track.artistName)
            putExtra("track_time", track.trackTimeMillis.toString())
            putExtra("album_cover", track.artworkUrl512)
            putExtra("collection_name", track.collectionName ?: "")
            putExtra("release_year", track.releaseYear ?: "")
            putExtra("genre", track.primaryGenreName ?: "")
            putExtra("country", track.country ?: "")
            putExtra("preview_url", track.previewUrl)
        }
        startActivity(intent)
    }

    private fun showError(message: String, imageRes: Int, showRetryButton: Boolean = false) {
        floatingContainer.visibility = View.VISIBLE
        errorImage.isVisible = true
        errorMessage.isVisible = true
        retryButton.isVisible = showRetryButton
        trackRecyclerView.isVisible = false
        Glide.with(this).load(imageRes).into(errorImage)
        errorMessage.text = message
    }

    // Функция для скрытия ошибки
    private fun hideError() {
        floatingContainer.isVisible = false
        errorImage.isVisible = false
        errorMessage.isVisible = false
        retryButton.isVisible = false
    }

    // Функция для отображения загрузки
    private fun showLoading(isLoading: Boolean) {
        progressBar.isVisible = isLoading
        trackRecyclerView.isVisible = !isLoading
    }

    // Функция для проверки сети
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Функция для очистки текста в поле ввода
    private fun clearInputText() {
        inputEditText.text.clear()  // Очищаем текст в поле
        inputEditText.clearFocus()  // Убираем фокус с поля ввода
        hideError()  // Скрываем ошибку, если она была показана
        showHistory()  // Показываем историю поиска
    }
}
