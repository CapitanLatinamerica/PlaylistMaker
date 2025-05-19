package com.practicum.playlistmaker.player.domain.model

// Возможные состояния плеера
sealed class PlayerState {
    object IDLE : PlayerState()            // Плеер ничего не делает
    object PREPARING : PlayerState()       // Идёт подготовка (загрузка) трека
    object PLAYING : PlayerState()         // Трек воспроизводится
    object PAUSED : PlayerState()          // Воспроизведение приостановлено
    object FINISHED : PlayerState()        // Воспроизведение завершено
    data class ERROR(val message: String) : PlayerState() // Ошибка воспроизведения
}