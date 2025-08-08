package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R

class MyAwesomePlaylistFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_playlist_awesome, container, false)
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            MyAwesomePlaylistFragment().apply {
            }
    }
}