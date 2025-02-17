package com.practicum.playlistmaker

import android.app.ActivityManager
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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var floatingContainer: ConstraintLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var errorImage: ImageView
    private lateinit var errorMessage: TextView
    private lateinit var retryButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesService::class.java)

    private val handler = Handler(Looper.getMainLooper())                                           //Создание Handler для управления задержками
    private val debounceRunnable = Runnable {                                                       // Runnable, который будет выполняться с задержкой
        performSearch(inputEditText.text.toString())
    }
    private val debounceDelay: Long = 500                                                           // Миллисекунды до выполнения запроса
    private var isNavigatingToPlayer = false                                                        // Флаг для проверки перехода на экран плеера

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация компонентов
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

        searchHistory = SearchHistory(getSharedPreferences("search_prefs", MODE_PRIVATE))

        // Обработка нажатия на кнопку "Назад" в тулбаре
        toolbar.setNavigationOnClickListener {
            finish()
        }

        showHistory() // Показываем историю

        // Очистка текста в поле поиска
        clearIcon.setOnClickListener {
            clearInputText()
            inputEditText.clearFocus()
            hideKeyboard() // Скрываем клавиатуру
            showHistory() // Показываем историю, если текст очищен
        }

        retryButton.setOnClickListener { performSearch(inputEditText.text.toString()) }

        // Очистка истории поиска
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            showHistory()
        }

        inputEditText.addTextChangedListener(object : TextWatcher {                                 // Обработка изменений текста в поле
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                showHistory()
                if (s.isNullOrEmpty()) {
                    hideKeyboard()
                    showHistory()
                } else {
                    hideError()
                    handler.removeCallbacks(debounceRunnable)
                    handler.postDelayed(debounceRunnable, debounceDelay) // Задержка перед запросом
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Обработка изменения фокуса
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistory()
                // Если поле ввода в фокусе, скрываем историю, кнопку и заголовок
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

        trackAdapter.setOnItemClickListener { track ->

            handler.removeCallbacks(debounceRunnable)                                               // Добавляем debounce для предотвращения открытия нескольких аудиоплееров
            handler.postDelayed(debounceRunnable, debounceDelay)                                    // Минимизация рисков открытия нескольких аудиоплееров
            isNavigatingToPlayer = true                                                             // Устанавливаем флаг перед переходом на экран плеера

            searchHistory.addTrack(track)                                                           // Добавляем трек в историю
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("track_name", track.trackName)
            intent.putExtra("artist_name", track.artistName)
            intent.putExtra("track_time", track.trackTimeMillis?.toString() ?: "")
            intent.putExtra("album_cover", track.artworkUrl512)
            intent.putExtra("collection_name", track.collectionName ?: "")
            intent.putExtra("release_year", track.releaseYear ?: "")
            intent.putExtra("genre", track.primaryGenreName ?: "")
            intent.putExtra("country", track.country ?: "")
            intent.putExtra("preview_url", track.previewUrl)

            // Проверяем, не активна ли уже эта активность
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningTasks = activityManager.getRunningTasks(1)
            if (runningTasks.isNotEmpty()) {
                val currentActivity = runningTasks[0].topActivity?.className
                if (currentActivity != AudioPlayerActivity::class.java.name) {
                    startActivity(intent)
                }
            } else {
                startActivity(intent)
            }
        }
    }

    private fun clearInputText() {
        inputEditText.text.clear()
        trackAdapter.updateTracks(emptyList()) // Очищаем список найденных треков
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0) // Скрываем клавиатуру
    }

    private fun performSearch(query: String, append: Boolean = false) {
        if (isNavigatingToPlayer) return                                                            // Если мы уже переходим на экран плеера, просто выходим
        val sanitizedQuery = query.trim()                                                           // Убираем только лишние пробелы (а это надо?)
        if (sanitizedQuery.isEmpty()) {
            Toast.makeText(this, getString(R.string.text_for_toast_write_something), Toast.LENGTH_SHORT).show()
            return
        }

        if (!isNetworkAvailable()) {
            showNetworkError()
            return
        } else {
            hideError()
        }

        showLoading(!append)

        fetchSearchResults(sanitizedQuery)
    }

    private fun fetchSearchResults(query: String) {
        iTunesService.searchSongs(query).enqueue(object : Callback<ITunesSearchResponse> {
            override fun onResponse(call: Call<ITunesSearchResponse>, response: Response<ITunesSearchResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val results = response.body()?.results.orEmpty().map { result ->
                        // Используем уже существующий объект Track, добавив новый параметр previewUrl
                        result.copy(previewUrl = result.previewUrl)
                    }
                    if (results.isNotEmpty()) {
                        hideError()
                        trackAdapter.updateTracks(results)
                    } else {
                        showError(
                            getString(R.string.nothing_founded),
                            R.drawable.ic_no_results,
                            showRetryButton = false
                        )
                    }
                } else {
                    showError(
                        getString(R.string.nothing_founded),
                        R.drawable.ic_no_results,
                        showRetryButton = false
                    )
                }
            }

            override fun onFailure(call: Call<ITunesSearchResponse>, t: Throwable) {
                showLoading(false)
                showError(
                    getString(R.string.no_internet_error),
                    R.drawable.ic_no_connection,
                    showRetryButton = true
                )
            }
        })
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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


    private fun hideError() {
        floatingContainer.visibility = View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.isVisible = isLoading // Показываем или скрываем ProgressBar
        trackRecyclerView.isVisible = !isLoading
        errorImage.isVisible = false
        errorMessage.isVisible = false
        retryButton.isVisible = false
    }

    private fun showNetworkError() {
        showError(
            getString(R.string.no_internet_error),
            R.drawable.ic_no_connection,
            showRetryButton = true
        )
    }
    private fun showHistory() {
        val history = searchHistory.getHistory()
        val isHistoryEmpty = history.isEmpty()

        // Проверяем, что поле ввода пустое
        if (inputEditText.text.isNullOrEmpty()) {
            // Показ или скрытие элементов в зависимости от наличия истории

            historyTitle.isVisible = !isHistoryEmpty
            clearHistoryButton.isVisible = !isHistoryEmpty
            trackRecyclerView.isVisible = true

            trackAdapter.updateTracks(history) // Показываем историю в RecyclerView

        } else {
            // Если поле ввода не пустое, скрываем историю
            historyTitle.isVisible = false
            clearHistoryButton.isVisible = false
            trackAdapter.updateTracks(emptyList()) // Очищаем список в адаптере
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        inputEditText.clearFocus()
    }

    override fun onResume() {
        super.onResume()
        isNavigatingToPlayer = false // Сбрасываем флаг, чтобы поиск не выполнялся при возвращении на экран
    }
}
