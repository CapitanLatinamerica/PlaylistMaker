package com.practicum.playlistmaker.search.data.dto

import com.practicum.playlistmaker.player.domain.Track

data class SearchScreenUiState(
    val screenState: SearchScreenState = SearchScreenState.IDLE,
    val searchResults: List<Track> = emptyList(),
    val history: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)


