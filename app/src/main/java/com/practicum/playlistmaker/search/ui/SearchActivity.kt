package com.practicum.playlistmaker.search.ui

import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.AudioPlayerActivity
import com.practicum.playlistmaker.player.TrackAdapter
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.domain.SearchTracksInteractor
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    // ViewModel для управления логикой поиска
    private lateinit var viewModel: SearchViewModel

    // Менеджер ввода для управления клавиатурой
    private lateinit var imm: InputMethodManager

    // Интерактор для работы с поиском треков
    private lateinit var searchTracksInteractor: SearchTracksInteractor

    // UI элементы
    private lateinit var floatingContainer: ConstraintLayout  // Контейнер для сообщений об ошибке
    private lateinit var retryButton: Button                 // Кнопка повтора при ошибке
    private lateinit var inputEditText: EditText            // Поле ввода поискового запроса
    private lateinit var clearIcon: ImageView               // Иконка очистки поля ввода
    private lateinit var trackRecyclerView: RecyclerView    // Список треков
    private lateinit var trackAdapter: TrackAdapter         // Адаптер для списка треков
    private lateinit var errorImage: ImageView              // Иконка ошибки
    private lateinit var errorMessage: TextView             // Текст ошибки
    private lateinit var historyTitle: TextView             // Заголовок истории поиска
    private lateinit var clearHistoryButton: Button         // Кнопка очистки истории
    private lateinit var progressBar: ProgressBar           // Индикатор загрузки

    // Handler и Runnable для реализации задержки при поиске (debounce)
    private val handler = Handler(Looper.getMainLooper())
    private val debounceDelay: Long = 500  // Задержка в миллисекундах
    private val debounceRunnable = Runnable {
        val query = inputEditText.text.toString()
        if (query.isBlank()) {
            viewModel.clearSearchResults()
        } else {
            viewModel.searchTracks(query)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        Log.d("SearchActivity", "onCreate started")

        // Инициализация ViewModel с фабрикой
        val searchInteractorImpl = Creator.provideSearchInteractor(this)
        val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(applicationContext)
        val factory = SearchViewModel.Factory(searchInteractorImpl, searchHistoryInteractor)
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)

        // Инициализация UI элементов
        initViews()

        // Настройка RecyclerView
        setupRecyclerView()

        // Настройка слушателей
        setupListeners()

        // Наблюдение за LiveData из ViewModel
        observeViewModel()
    }

    private fun initViews() {
        // Получение ссылок на все UI элементы
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

        // Настройка тулбара
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        // Инициализация адаптера с пустым списком
        trackAdapter = TrackAdapter(mutableListOf())

        // Настройка менеджера компоновки
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter

        // Установка слушателя кликов по элементам списка
        trackAdapter.setOnItemClickListener { track ->
            viewModel.saveTrackToHistory(track)
            openAudioPlayer(track)
        }
    }

    private fun setupListeners() {
        // Очистка поля ввода
        clearIcon.setOnClickListener {
            clearInputText()
            viewModel.clearSearchResults()
            hideKeyboard()
        }

        // Очистка истории поиска
        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        // Повторный поиск при ошибке
        retryButton.setOnClickListener {
            val query = inputEditText.text.toString()
            viewModel.searchTracks(query)
//            performSearch(inputEditText.text.toString())
        }

        // Слушатель изменений текста в поле ввода
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Показать/скрыть иконку очистки
                clearIcon.isVisible = !s.isNullOrEmpty()

                // Отменить предыдущий запрос
                handler.removeCallbacks(debounceRunnable)

                // Запустить новый запрос с задержкой
                handler.postDelayed(debounceRunnable, debounceDelay)
            }

            override fun afterTextChanged(s: Editable?) {
                // Не используется
            }
        })
    }

    private fun observeViewModel() {
        // Наблюдение за результатами поиска
        viewModel.searchResults.observe(this, Observer { tracks ->
            Log.d("SearchActivity", "Search results updated: ${tracks.size} tracks")
            trackAdapter.updateTracks(tracks)

            // Показываем ошибку, если результатов нет
            if (tracks.isEmpty() && inputEditText.text.isNotBlank()) {
                showError(
                    getString(R.string.nothing_founded),
                    R.drawable.ic_no_results,
                    showRetryButton = false
                )
            } else {
                hideError()
            }
        })

        // Наблюдение за состоянием загрузки
        viewModel.isLoading.observe(this, Observer { isLoading ->
            Log.d("SearchActivity", "isLoading: $isLoading")
            progressBar.isVisible = isLoading
        })

        // Наблюдение за ошибками
        viewModel.error.observe(this, Observer { error ->
            Log.d("SearchActivity", "Error: $error")
            errorMessage.text = error
            errorMessage.isVisible = error.isNotEmpty()
        })

        // Наблюдение за историей поиска
        viewModel.history.observe(this) { history ->
            clearHistoryButton.isVisible = history.isNotEmpty()
            if (history != null && history.isNotEmpty()) {
                historyTitle.isVisible = false
                clearHistoryButton.isVisible = false
                trackAdapter.updateTracks(history)
            } else {
                historyTitle.isVisible = history.isNotEmpty()
                trackAdapter.updateTracks(emptyList())
                trackRecyclerView.isVisible = true
            }
        }
    }

/*    private fun performSearch(query: String) {

        Log.d("SearchActivity", "Performing search for query: $query")
        if (query.isBlank()) {
            Log.d("SearchActivity", "Search query is blank, skipping search.")
            return
        }

        showLoading(true)                                                                  // Показываем индикатор загрузки

        lifecycleScope.launch {
            try {
                val tracks = searchTracksInteractor.searchTracks(query)                             // Получаем список треков через ViewModel
                Log.d("SearchActivity", "Received tracks: ${tracks.size}")                 // Логируем количество полученных треков
                showLoading(false)                                                         // Прячем индикатор загрузки

                if (tracks.isEmpty()) {
                    Log.d("SearchActivity", "No tracks found.")
                    showError(
                        getString(R.string.nothing_founded),
                        R.drawable.ic_no_results,
                        showRetryButton = false
                    )
                } else {
                    trackAdapter.updateTracks(tracks)
                    hideError()
                }
            } catch (e: Exception) {
                showLoading(false)
                Log.e("SearchActivity", "Error during search: ${e.message}")
                showError(
                    getString(R.string.nothing_founded),
                    R.drawable.ic_no_results,
                    showRetryButton = true
                )
            }
        }
    }*/

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
        Log.d("SearchActivity", "Displaying error message: $message")
        floatingContainer.visibility = View.VISIBLE                                                 // Показываем контейнер с ошибкой
        errorImage.isVisible = true                                                                 // Показываем иконку ошибки
        errorMessage.isVisible = true                                                               // Показываем текст ошибки
        errorMessage.text = message                                                                 // Текст ошибки
        retryButton.isVisible = showRetryButton                                                     // Показываем кнопку повторного поиска
        trackRecyclerView.isVisible = false                                                         // Скрываем RecyclerView
        Glide.with(this).load(imageRes).into(errorImage)                                     // Загружаем иконку ошибки
    }

    private fun hideError() {
        floatingContainer.isVisible = false
        errorImage.isVisible = false
        errorMessage.isVisible = false
        retryButton.isVisible = false
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.isVisible = isLoading
        trackRecyclerView.isVisible = !isLoading
    }

    private fun clearInputText() {
        inputEditText.text.clear()
        inputEditText.clearFocus()
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        viewModel.searchTracks("")
        clearIcon.isVisible = false
    }

    private fun hideKeyboard() {
        val imm = getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }
}