package com.practicum.playlistmaker.util

import android.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R

fun getTrackCountText(count: Int): String {
        val remainder10 = count % 10
        val remainder100 = count % 100

        val word = when {
            remainder100 in 11..14 -> "треков"
            remainder10 == 1 -> "трек"
            remainder10 in 2..4 -> "трека"
            else -> "треков"
        }

        return "$count $word"
    }

fun Fragment.showDeletePlaylistDialog(
    title: String = getString(R.string.playlist_delete),
    message: String = getString(R.string.delete_playlist_message),
    positiveText: String = getString(R.string.delete),
    negativeText: String = getString(R.string.alert_dialog_negative),
    onDeleteCallback: () -> Unit
) {
    val dialog = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ -> onDeleteCallback() }
        .setNegativeButton(negativeText, null)
        .show()

    // Задаём цвет кнопок после показа диалога
    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
    val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

    val blueColor = ContextCompat.getColor(requireContext(), R.color.yp_blue)
    positiveButton.setTextColor(blueColor)
    negativeButton.setTextColor(blueColor)
}

// Специализированный диалог для удаления трека
fun Fragment.showDeleteTrackDialog(
    trackName: String,
    onDeleteCallback: () -> Unit
) {
    val title = getString(R.string.delete_track_title)
    val message = getString(R.string.delete_track_message, trackName)
    showDeleteConfirmationDialog(
        title = title,
        message = message,
        onConfirm = onDeleteCallback
    )
}

fun Fragment.showDeleteConfirmationDialog(
    title: String,
    message: String,
    positiveText: String = getString(R.string.delete),
    negativeText: String = getString(R.string.alert_dialog_negative),
    onConfirm: () -> Unit
) {
    val dialog = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ -> onConfirm() }
        .setNegativeButton(negativeText, null)
        .show()

    val blueColor = ContextCompat.getColor(requireContext(), R.color.yp_blue)
    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(blueColor)
    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(blueColor)
}



