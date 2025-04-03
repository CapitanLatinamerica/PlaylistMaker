package com.practicum.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _history = MutableLiveData<List<Track>>()
    val history: LiveData<List<Track>> = _history

    init {
        loadSearchHistory()
    }

    fun searchTracks(query: String) {
        if (query.isBlank()) return

        _isLoading.value = true

        searchInteractor.searchTracks(query) { tracks ->
            _isLoading.postValue(false)
            _searchResults.postValue(tracks)
        }
    }

    fun loadSearchHistory() {
        _history.value = searchHistoryInteractor.getHistory()
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.saveTrack(track)
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
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
