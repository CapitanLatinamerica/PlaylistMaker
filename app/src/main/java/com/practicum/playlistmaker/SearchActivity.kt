package com.practicum.playlistmaker

import android.content.Context
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var floatingContainer: ConstraintLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var errorImage: ImageView
    private lateinit var errorMessage: TextView
    private lateinit var retryButton: Button
    private var currentOffset = 0
    private var isLoadingMore = false
    private var allResultsLoaded = false

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesService::class.java)

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

        trackAdapter = TrackAdapter(mutableListOf(), this)
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter

        // Обработка нажатия на кнопку "Назад" в тулбаре
        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearIcon.setOnClickListener { clearInputText() }

        retryButton.setOnClickListener { performSearch(inputEditText.text.toString()) }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (!s.isNullOrEmpty()) {
                    hideError() // Убираем ошибку, так как текст изменён
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Обработка нажатия кнопки "Done"
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = inputEditText.text.toString()
                if (query.isNotEmpty()) {
                    performSearch(query)
                } else {
                    Toast.makeText(this, getString(R.string.text_for_toast_write_something), Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        trackRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoadingMore && !allResultsLoaded &&
                    visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                    firstVisibleItemPosition >= 0) {
                    isLoadingMore = true
                    performSearch(inputEditText.text.toString(), append = true)
                }
            }
        })

    }

    private fun clearInputText() {
        inputEditText.text.clear()
        trackAdapter.updateTracks(emptyList()) // Очищаем список найденных треков
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0) // Скрываем клавиатуру
    }

    private fun performSearch(query: String, append: Boolean = false) {
        val sanitizedQuery = query.trim() // Убираем только лишние пробелы
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

        if (!append) {
            currentOffset = 0
            allResultsLoaded = false
            trackAdapter.updateTracks(emptyList()) // Сбрасываем список при новом поиске
        }

        if (allResultsLoaded) return

        showLoading(!append)

        iTunesService.searchSongs(sanitizedQuery).enqueue(object : Callback<ITunesSearchResponse> {
            override fun onResponse(call: Call<ITunesSearchResponse>, response: Response<ITunesSearchResponse>) {
                showLoading(false)
                isLoadingMore = false
                if (response.isSuccessful) {
                    val results = response.body()?.results.orEmpty()
                    if (results.isNotEmpty()) {
                        hideError()
                        trackAdapter.updateTracks(if (append) trackAdapter.getTracks() + results else results)
                        currentOffset += results.size
                    } else {
                        allResultsLoaded = true // Если новых результатов нет, больше не загружаем
                        if (!append) {
                            showError(
                                getString(R.string.nothing_founded),
                                R.drawable.ic_no_results,
                                showRetryButton = false
                            )
                        }
                    }
                } else {
                    if (!append) {
                        showError(
                            getString(R.string.nothing_founded),
                            R.drawable.ic_no_results,
                            showRetryButton = false
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ITunesSearchResponse>, t: Throwable) {
                showLoading(false)
                isLoadingMore = false
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
        findViewById<ProgressBar>(R.id.progress_bar).isVisible = isLoading
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

}