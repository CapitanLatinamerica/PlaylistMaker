package com.practicum.playlistmaker.search.ui

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
import com.practicum.playlistmaker.player.ui.view.AudioPlayerActivity
import com.practicum.playlistmaker.player.TrackAdapter
import com.practicum.playlistmaker.player.data.Constants
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {


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
        if (query.isBlank()) {
            viewModel.clearSearchResults()
        } else {
            viewModel.searchTracks(query)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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
            finish()
        }
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(mutableListOf())
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter

        trackAdapter.setOnItemClickListener { track ->
            viewModel.saveTrackToHistory(track)
            openAudioPlayer(track)
        }
    }

    private fun setupListeners() {

        clearIcon.setOnClickListener {
            clearInputText()
            viewModel.clearSearchResults()
            hideKeyboard()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        retryButton.setOnClickListener {
            val query = inputEditText.text.toString()
            viewModel.searchTracks(query)
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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

        viewModel.screenState.observe(this) { state ->
            when (state) {
                SearchViewModel.SearchScreenState.HISTORY -> showHistoryState()
                SearchViewModel.SearchScreenState.RESULTS -> showResultsState()
                SearchViewModel.SearchScreenState.EMPTY_RESULTS -> showEmptyResultsState()
                SearchViewModel.SearchScreenState.ERROR -> showErrorState()
                SearchViewModel.SearchScreenState.IDLE -> {}
            }
        }

        viewModel.searchResults.observe(this) { tracks ->
            trackAdapter.updateTracks(tracks)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.isVisible = isLoading
            trackRecyclerView.isVisible = !isLoading
        }

        viewModel.history.observe(this) { history ->
            trackAdapter.updateTracks(history)
        }
    }

    private fun showHistoryState() {
        historyTitle.isVisible = viewModel.history.value?.isNotEmpty() == true
        clearHistoryButton.isVisible = viewModel.history.value?.isNotEmpty() == true
        trackRecyclerView.isVisible = true
        hideError()
    }

    private fun showResultsState() {
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        trackRecyclerView.isVisible = true
        hideError()
    }

    private fun showEmptyResultsState() {
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        showError(
            getString(R.string.nothing_founded),
            R.drawable.ic_no_results,
            showRetryButton = false
        )
    }

    private fun showErrorState() {
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        showError(
            getString(R.string.no_internet_message),
            R.drawable.ic_no_connection,
            showRetryButton = true
        )
    }

    private fun openAudioPlayer(track: Track) {
        startActivity(Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(Constants.Extra.TRACK_NAME, track.trackName)
            putExtra(Constants.Extra.ARTIST_NAME, track.artistName)
            putExtra(Constants.Extra.TRACK_TIME, track.trackTimeMillis.toString())
            putExtra(Constants.Extra.ALBUM_COVER, track.getArtworkUrl512())
            putExtra(Constants.Extra.COLLECTION_NAME, track.collectionName ?: "")
            putExtra(Constants.Extra.RELEASE_YEAR, track.releaseDate?.takeIf { it.isNotEmpty() }?.split("-")?.get(0) ?: "")
            putExtra(Constants.Extra.GENRE, track.primaryGenreName ?: "")
            putExtra(Constants.Extra.COUNTRY, track.country ?: "")
            putExtra(Constants.Extra.PREVIEW_URL, track.previewUrl)
        })
    }

    private fun showError(message: String, imageRes: Int, showRetryButton: Boolean) {
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
        viewModel.loadSearchHistory()
        clearIcon.isVisible = false
    }

    private fun hideKeyboard() {
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }
}