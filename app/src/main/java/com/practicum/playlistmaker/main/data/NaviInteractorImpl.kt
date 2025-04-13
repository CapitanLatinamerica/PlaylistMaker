package com.practicum.playlistmaker.main.data

import android.app.Activity
import android.content.Intent
import com.practicum.playlistmaker.main.domain.NaviInteractor
import com.practicum.playlistmaker.player.ui.view.MediaActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity

class NaviInteractorImpl(private val activity: Activity) : NaviInteractor {
    override fun openSearch() {
        activity.startActivity(Intent(activity, SearchActivity::class.java))
    }

    override fun openMedia() {
        activity.startActivity(Intent(activity, MediaActivity::class.java))
    }

    override fun openSettings() {
        activity.startActivity(Intent(activity, SettingsActivity::class.java))
    }
}