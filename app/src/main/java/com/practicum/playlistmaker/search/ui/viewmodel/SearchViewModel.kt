package com.practicum.playlistmaker.search.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.SearchInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    enum class SearchScreenState {
        HISTORY, RESULTS, EMPTY_RESULTS, ERROR, IDLE
    }

    private val _state = MutableLiveData<SearchState>(SearchState.Idle)
    val state: LiveData<SearchState> = _state

    private val _screenState = MutableLiveData<SearchScreenState>()
    val screenState: LiveData<SearchScreenState> = _screenState

    private val _history = MutableLiveData<List<Track>>()
    val history: LiveData<List<Track>> = _history

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _searchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> = _searchResults

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var currentQuery: String = ""
    private var searchJob: Job? = null

    init {
        Log.d(TAG, "ViewModel initialized")
        loadSearchHistory()
    }

    fun loadSearchHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNullOrEmpty()) {
            _history.value = emptyList()
        } else {
            _history.value = history
        }
        _screenState.value = SearchScreenState.HISTORY
    }

    fun searchTracks(query: String) {
        currentQuery = query

        if (query.isBlank()) {
            loadSearchHistory()
            return
        }

        _isLoading.value = true
        _screenState.value = SearchScreenState.IDLE
        _history.value = emptyList()
        _error.value = ""

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val tracks = searchInteractor.searchTracks(query)

                if (tracks.isEmpty()) {
                    _screenState.value = SearchScreenState.EMPTY_RESULTS
                    _error.value = "NO_RESULTS"
                } else {
                    _screenState.value = SearchScreenState.RESULTS
                }
                _searchResults.value = tracks
            } catch (e: Exception) {
                _screenState.value = SearchScreenState.ERROR
                _error.value = "NETWORK_ERROR"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveTrackToHistory(track: Track) {
        Log.d(TAG, "Saving track to history: ${track.trackName}")
        viewModelScope.launch {
            try {
                searchHistoryInteractor.saveTrack(track)
                Log.d(TAG, "Track saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving track", e)
            }
        }
    }

    fun clearSearchHistory() {
        Log.d(TAG, "Clearing search history")
        viewModelScope.launch {
            searchHistoryInteractor.clearHistory()
            loadSearchHistory()
        }
    }

    fun clearSearchResults() {
        Log.d(TAG, "Clearing search results")
        _searchResults.value = emptyList()
        loadSearchHistory()
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

sealed class SearchState {
    data class History(val tracks: List<Track>) : SearchState()
    data class Results(val tracks: List<Track>, val query: String) : SearchState()
    object EmptyResults : SearchState()
    object Error : SearchState()
    object Loading : SearchState()
    object Idle : SearchState()
}