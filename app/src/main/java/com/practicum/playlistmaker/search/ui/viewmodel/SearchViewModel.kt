package com.practicum.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.dto.SearchScreenState
import com.practicum.playlistmaker.search.data.dto.SearchScreenUiState
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.SearchInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    // Добавляем сохранение истории в состоянии
    private val _uiState = MutableStateFlow(
        SearchScreenUiState(
            screenState = SearchScreenState.HISTORY,
            history = searchHistoryInteractor.getHistory()
        )
    )
    val uiState: StateFlow<SearchScreenUiState> = _uiState

    private var currentQuery: String = ""
    private var searchJob: Job? = null
    private var isHistoryLoaded = false

    init {
        loadSearchHistory()
    }

    fun loadInitialHistory() {
        if (isHistoryLoaded) return

        viewModelScope.launch {
            val history = searchHistoryInteractor.getHistory()
            _uiState.value = _uiState.value.copy(
                screenState = SearchScreenState.HISTORY,
                history = history,
                isLoading = false
            )
            isHistoryLoaded = true
        }
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            val history = searchHistoryInteractor.getHistory()
            _uiState.value = _uiState.value.copy(
                history = history,
                screenState = if (history.isEmpty()) SearchScreenState.IDLE
                else SearchScreenState.HISTORY
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
            searchInteractor.searchTracks(query)
                .catch { exception ->
                    _uiState.value = SearchScreenUiState(
                        isLoading = false,
                        screenState = SearchScreenState.ERROR,
                        searchResults = emptyList(),
                        history = emptyList(),
                        errorMessage = "NETWORK_ERROR"
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { tracks ->
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
                        },
                        onFailure = {
                            _uiState.value = SearchScreenUiState(
                                isLoading = false,
                                screenState = SearchScreenState.ERROR,
                                searchResults = emptyList(),
                                history = emptyList(),
                                errorMessage = "NETWORK_ERROR"
                            )
                        }
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
        // Не очищаем историю, только результаты поиска
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            screenState = if (_uiState.value.history.isEmpty()) SearchScreenState.IDLE
            else SearchScreenState.HISTORY
        )
    }
}