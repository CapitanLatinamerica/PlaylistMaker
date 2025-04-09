package com.practicum.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.dto.SearchScreenState
import com.practicum.playlistmaker.search.data.dto.SearchScreenUiState
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.SearchInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchScreenUiState())
    val uiState: StateFlow<SearchScreenUiState> = _uiState


    private var currentQuery: String = ""
    private var searchJob: Job? = null

    init {
        loadSearchHistory()
    }

    fun loadSearchHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNullOrEmpty()) {
            _uiState.value = SearchScreenUiState(
                isLoading = false,
                screenState = SearchScreenState.HISTORY,
                searchResults = emptyList(),
                history = emptyList(),
                errorMessage = null
            )
        } else {
            _uiState.value = SearchScreenUiState(
                isLoading = false,
                screenState = SearchScreenState.HISTORY,
                searchResults = emptyList(),
                history = history,
                errorMessage = null
            )
        }
    }

    fun searchTracks(query: String) {
        currentQuery = query

        if (query.isBlank()) {
            loadSearchHistory()
            return
        }

        _uiState.value = SearchScreenUiState(
            isLoading = true,
            screenState = SearchScreenState.IDLE,
            searchResults = emptyList(),
            history = emptyList(),
            errorMessage = null
        )

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val tracks = searchInteractor.searchTracks(query)

                if (tracks.isEmpty()) {
                    _uiState.value = SearchScreenUiState(
                        isLoading = false,
                        screenState = SearchScreenState.EMPTY_RESULTS,
                        searchResults = emptyList(),
                        history = emptyList(),
                        errorMessage = "NO_RESULTS"
                    )
                } else {
                    _uiState.value = SearchScreenUiState(
                        isLoading = false,
                        screenState = SearchScreenState.RESULTS,
                        searchResults = tracks,
                        history = emptyList(),
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = SearchScreenUiState(
                    isLoading = false,
                    screenState = SearchScreenState.ERROR,
                    searchResults = emptyList(),
                    history = emptyList(),
                    errorMessage = "NETWORK_ERROR"
                )
            }
        }
    }

    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.saveTrack(track)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.clearHistory()
            loadSearchHistory()
        }
    }

    fun clearSearchResults() {
        _uiState.value = SearchScreenUiState(
            isLoading = false,
            screenState = SearchScreenState.HISTORY,
            searchResults = emptyList(),
            history = emptyList(),
            errorMessage = null
        )
    }

    class Factory(
        private val searchInteractor: SearchInteractor,
        private val searchHistoryInteractor: SearchHistoryInteractor
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(searchInteractor, searchHistoryInteractor) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}