package com.practicum.playlistmaker.player.ui

import java.text.SimpleDateFormat
import java.util.Locale

object TimeFormatter {
    fun formatTrackTime(millis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(millis)
            .replace("^0", "")
    }
}