package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel.MyAwesomePlaylistFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyAwesomePlaylistFragment : Fragment() {

    private val viewModel: MyAwesomePlaylistFragmentViewModel by viewModel()
    private val args: MyAwesomePlaylistFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_playlist_awesome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = args.playlistId
        viewModel.loadPlaylistDetails(playlistId)

        // Подключаем данные к UI
        viewModel.playlistDetails.observe(viewLifecycleOwner) { playlist ->
            // Отображаем детали плейлиста
        }
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            MyAwesomePlaylistFragment().apply {
            }
    }
}