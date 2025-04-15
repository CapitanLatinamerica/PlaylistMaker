package com.practicum.playlistmaker.search.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.TrackAdapter
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.dto.SearchScreenState
import com.practicum.playlistmaker.search.data.dto.SearchScreenUiState
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter

    // UI элементы
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorImage: ImageView
    private lateinit var errorMessage: TextView
    private lateinit var retryButton: Button
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var floatingContainer: ConstraintLayout

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация UI
        initViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
        }

        inputEditText = view.findViewById(R.id.findEditText)
        clearIcon = view.findViewById(R.id.clearTextIcon)
        trackRecyclerView = view.findViewById(R.id.trackRecyclerView)
        progressBar = view.findViewById(R.id.progress_bar)
        errorImage = view.findViewById(R.id.error_image)
        errorMessage = view.findViewById(R.id.error_message)
        retryButton = view.findViewById(R.id.retry_button)
        historyTitle = view.findViewById(R.id.historyTitle)
        clearHistoryButton = view.findViewById(R.id.clearHistoryButton)
        floatingContainer = view.findViewById(R.id.floating_container)
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(mutableListOf())
        trackRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                render(state)
            }
        }
    }

    private fun render(state: SearchScreenUiState) {
        // Аналогично SearchActivity
        progressBar.isVisible = state.isLoading
        trackRecyclerView.isVisible = !state.isLoading
        clearIcon.isVisible = inputEditText.text.isNotEmpty()

        when (state.screenState) {
            SearchScreenState.HISTORY -> {
                trackAdapter.updateTracks(state.history)
                historyTitle.isVisible = state.history.isNotEmpty()
                clearHistoryButton.isVisible = state.history.isNotEmpty()
                hideError()
            }
            SearchScreenState.RESULTS -> {
                trackAdapter.updateTracks(state.searchResults)
                historyTitle.isVisible = false
                clearHistoryButton.isVisible = false
                hideError()
            }
            SearchScreenState.EMPTY_RESULTS -> {
                showError(getString(R.string.nothing_founded), R.drawable.ic_no_results, false)
            }
            SearchScreenState.ERROR -> {
                showError(getString(R.string.no_internet_message), R.drawable.ic_no_connection, true)
            }
            SearchScreenState.IDLE -> {}
        }
    }

    private fun openAudioPlayer(track: Track) {
        // Тут будет навигация к AudioPlayerActivity (пока оставляем как есть)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    private fun clearInputText() {
        inputEditText.text.clear()
        inputEditText.clearFocus()
        hideKeyboard()
        viewModel.searchTracks("")
        viewModel.loadSearchHistory()
        clearIcon.isVisible = false
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
}