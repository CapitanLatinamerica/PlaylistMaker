package com.practicum.playlistmaker.search.ui.viewmodel

import android.util.Log
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
    private val searchInteractor: SearchInteractor,                         // Для выполнения поиска
    private val searchHistoryInteractor: SearchHistoryInteractor            // Для работы с историей поиска
) : ViewModel() {

    // Хранилище текущего UI-состояния экрана
    private val _uiState = MutableStateFlow(
        SearchScreenUiState(
            screenState = SearchScreenState.HISTORY,                // По умолчанию показываем историю
            history = searchHistoryInteractor.getHistory()         // Загружаем текущую историю
        )
    )

    val uiState: StateFlow<SearchScreenUiState> = _uiState        // Публичное неизменяемое состояние

    private var currentQuery: String = ""                         // Последний введённый поисковый запрос
    private var searchJob: Job? = null                            // Активная coroutine job для поиска
    private var isHistoryLoaded = false                           // Флаг, чтобы не загружать историю повторно

    init {
        loadSearchHistory()                                       // При создании ViewModel загружаем историю
    }

    // Метод для загрузки истории один раз (при первом запуске)
    fun loadInitialHistory() {
        if (isHistoryLoaded) return                               // Защита от повторной загрузки

        viewModelScope.launch {
            val history = searchHistoryInteractor.getHistory()
                .sortedWith(compareByDescending<Track> { it.isFavorite }
                .thenBy { it.addedAt })
            _uiState.value = _uiState.value.copy(
                screenState = SearchScreenState.HISTORY,
                history = history,
                isLoading = false
            )
            isHistoryLoaded = true
        }
    }

    // Загружаем историю поиска
    fun loadSearchHistory() {
        viewModelScope.launch {
            Log.d("SearchViewModel", "Loading search history")  // Логируем загрузку истории
            val history = searchHistoryInteractor.getHistory()
                .sortedWith(compareByDescending<Track> { it.isFavorite }
                    .thenByDescending { it.addedAt })
            _uiState.value = _uiState.value.copy(
                history = history,
                screenState = if (history.isEmpty()) SearchScreenState.IDLE
                else SearchScreenState.HISTORY
            )
            Log.d("SearchViewModel", "Search history loaded, history size: ${history.size}")
        }
    }


    // Выполнение поиска треков по запросу
    fun searchTracks(query: String) {
        currentQuery = query

        // Если строка пустая — показываем историю
        if (query.isBlank()) {
            loadSearchHistory()
            return
        }

        // Показываем прогресс (загрузка)
        _uiState.value = SearchScreenUiState(
            isLoading = true,
            screenState = SearchScreenState.IDLE,
            searchResults = emptyList(),
            history = emptyList(),
            errorMessage = null
        )

        // Отменяем предыдущий поиск (если он был)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {                                     // Запускаем новый поиск
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
                    // Получаем результат из потока Flow<Result<List<Track>>>
                    result.fold(
                        onSuccess = { tracks ->
                            // Пустой результат
                            if (tracks.isEmpty()) {
                                _uiState.value = SearchScreenUiState(
                                    isLoading = false,
                                    screenState = SearchScreenState.EMPTY_RESULTS,
                                    searchResults = emptyList(),
                                    history = emptyList(),
                                    errorMessage = "NO_RESULTS"
                                )
                            } else {
                                // Успешный поиск
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
                            // Ошибка при получении результатов
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

    // Сохранение трека в историю
    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
                searchHistoryInteractor.saveTrack(track)
        }
    }

    // Очистка истории поиска
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