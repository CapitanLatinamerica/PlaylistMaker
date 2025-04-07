package com.practicum.playlistmaker.search.ui.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.SearchInteractor
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    // LiveData для истории поиска
    private val _history = MutableLiveData<List<Track>>()
    val history: LiveData<List<Track>> = _history

    // LiveData для состояния загрузки
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData для результатов поиска
    private val _searchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> = _searchResults

    // LiveData для ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadSearchHistory()  // Загрузка истории при инициализации
    }

    // Загрузка истории поиска
    fun loadSearchHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNullOrEmpty()) {
            _history.value = emptyList()
        } else {
            _history.value = history
        }
    }

    // Поиск треков по запросу
    fun searchTracks(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val tracks = searchInteractor.searchTracks(query)

                if (tracks.isEmpty()) {
                    _error.postValue(R.string.nothing_founded.toString())
                } else {
                    _error.postValue("")
                }

                _searchResults.postValue(tracks)
            } catch (e: Exception) {
                _error.postValue("Error occurred: ${e.message}")
                _searchResults.postValue(emptyList())
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Сохранение трека в историю
    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
            try {
                Log.d("SearchViewModel", "Saving track to history: ${track.trackName}")
                searchHistoryInteractor.saveTrack(track)
                Log.d("SearchViewModel", "Track saved successfully")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error saving track to history: ${e.message}", e)
            }
        }
    }

    // Очистка истории поиска
    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.clearHistory()
            loadSearchHistory()
        }
    }

 /*   // Функция для проверки сети
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }*/

    // Очистка результатов поиска
    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    // Фабрика для создания ViewModel
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