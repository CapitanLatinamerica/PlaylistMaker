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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.AudioPlayerActivity
import com.practicum.playlistmaker.player.TrackAdapter
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SearchActivity"
    }

    private lateinit var viewModel: SearchViewModel
    private lateinit var imm: InputMethodManager

    // UI элементы
    private lateinit var floatingContainer: ConstraintLayout
    private lateinit var retryButton: Button
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var errorImage: ImageView
    private lateinit var errorMessage: TextView
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    private val handler = Handler(Looper.getMainLooper())
    private val debounceDelay: Long = 500
    private val debounceRunnable = Runnable {
        val query = inputEditText.text.toString()
        Log.d(TAG, "Executing search for: '$query'")
        if (query.isBlank()) {
            viewModel.clearSearchResults()
        } else {
            viewModel.searchTracks(query)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        Log.d(TAG, "Activity created")

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        // Инициализация ViewModel
        val searchInteractor = Creator.provideSearchInteractor(this)
        val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(applicationContext)
        val factory = SearchViewModel.Factory(searchInteractor, searchHistoryInteractor)
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)

        initViews()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun initViews() {
        Log.d(TAG, "Initializing views")
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

        toolbar.setNavigationOnClickListener {
            Log.d(TAG, "Back button clicked")
            finish()
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")
        trackAdapter = TrackAdapter(mutableListOf())
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter

        trackAdapter.setOnItemClickListener { track ->
            Log.d(TAG, "Track clicked: ${track.trackName}")
            viewModel.saveTrackToHistory(track)
            openAudioPlayer(track)
        }
    }

    private fun setupListeners() {
        Log.d(TAG, "Setting up listeners")

        clearIcon.setOnClickListener {
            Log.d(TAG, "Clear button clicked")
            clearInputText()
            viewModel.clearSearchResults()
            hideKeyboard()
        }

        clearHistoryButton.setOnClickListener {
            Log.d(TAG, "Clear history button clicked")
            viewModel.clearSearchHistory()
        }

        retryButton.setOnClickListener {
            val query = inputEditText.text.toString()
            Log.d(TAG, "Retry button clicked, query: '$query'")
            viewModel.searchTracks(query)
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "Text changed: '${s.toString()}'")
                clearIcon.isVisible = !s.isNullOrEmpty()
                handler.removeCallbacks(debounceRunnable)

                if (!s.isNullOrEmpty()) {
                    historyTitle.isVisible = false
                    clearHistoryButton.isVisible = false
                }

                handler.postDelayed(debounceRunnable, debounceDelay)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        Log.d(TAG, "Setting up observers")

        viewModel.screenState.observe(this) { state ->
            Log.d(TAG, "Screen state changed: $state")
            when (state) {
                SearchViewModel.SearchScreenState.HISTORY -> showHistoryState()
                SearchViewModel.SearchScreenState.RESULTS -> showResultsState()
                SearchViewModel.SearchScreenState.EMPTY_RESULTS -> showEmptyResultsState()
                SearchViewModel.SearchScreenState.ERROR -> showErrorState()
                SearchViewModel.SearchScreenState.IDLE -> {}
            }
        }

        viewModel.searchResults.observe(this) { tracks ->
            Log.d(TAG, "Search results updated: ${tracks.size} tracks")
            trackAdapter.updateTracks(tracks)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            Log.d(TAG, "Loading state: $isLoading")
            progressBar.isVisible = isLoading
            trackRecyclerView.isVisible = !isLoading
        }

        viewModel.history.observe(this) { history ->
            Log.d(TAG, "History updated: ${history.size} items")
            trackAdapter.updateTracks(history)
        }
    }

    private fun showHistoryState() {
        Log.d(TAG, "Showing history state")
        historyTitle.isVisible = viewModel.history.value?.isNotEmpty() == true
        clearHistoryButton.isVisible = viewModel.history.value?.isNotEmpty() == true
        trackRecyclerView.isVisible = true
        hideError()
    }

    private fun showResultsState() {
        Log.d(TAG, "Showing results state")
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        trackRecyclerView.isVisible = true
        hideError()
    }

    private fun showEmptyResultsState() {
        Log.d(TAG, "Showing empty results state")
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        showError(
            getString(R.string.nothing_founded),
            R.drawable.ic_no_results,
            showRetryButton = false
        )
    }

    private fun showErrorState() {
        Log.d(TAG, "Showing error state")
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        showError(
            getString(R.string.no_internet_message),
            R.drawable.ic_no_connection,
            showRetryButton = true
        )
    }

    private fun openAudioPlayer(track: Track) {
        Log.d("SEARCH_DEBUG", "Opening player for track: ${track.trackName}, URL: ${track.previewUrl}")
        startActivity(Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra("track_name", track.trackName)
            putExtra("artist_name", track.artistName)
            putExtra("track_time", track.trackTimeMillis.toString())
            putExtra("album_cover", track.getArtworkUrl512())
            putExtra("collection_name", track.collectionName ?: "")
            putExtra("release_year", track.releaseYear ?: "")
            putExtra("genre", track.primaryGenreName ?: "")
            putExtra("country", track.country ?: "")
            putExtra("preview_url", track.previewUrl)
        })
    }

    private fun showError(message: String, imageRes: Int, showRetryButton: Boolean) {
        Log.d(TAG, "Showing error: $message")
        floatingContainer.visibility = View.VISIBLE
        errorImage.isVisible = true
        errorMessage.isVisible = true
        errorMessage.text = message
        retryButton.isVisible = showRetryButton
        trackRecyclerView.isVisible = false
        Glide.with(this).load(imageRes).into(errorImage)
    }

    private fun hideError() {
        floatingContainer.isVisible = false
        errorImage.isVisible = false
        errorMessage.isVisible = false
        retryButton.isVisible = false
    }

    private fun clearInputText() {
        inputEditText.text.clear()
        inputEditText.clearFocus()
        hideKeyboard()
        viewModel.searchTracks("")
        viewModel.loadSearchHistory() // Загружаем историю поиска
        clearIcon.isVisible = false
    }

    private fun hideKeyboard() {
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }
}