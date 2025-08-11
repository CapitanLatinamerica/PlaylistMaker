package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.MediaFragmentDirections
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi
import com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel ()

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_playlists, container, false)

        // Получаем контроллер навигации
        navController = NavHostFragment.findNavController(this)

        val createPlaylistButton = view.findViewById<Button>(R.id.createPlaylist)
        createPlaylistButton.setOnClickListener {
            viewModel.onCreatePlaylistClicked()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadPlaylists()

        val recyclerView = view.findViewById<RecyclerView>(R.id.playlistRV)
        val placeholderLayout = view.findViewById<LinearLayout>(R.id.playlists_layout)
        val scrollView = view.findViewById<NestedScrollView>(R.id.playlistScroll)

        val adapter = PlaylistAdapter { playlist ->
            onPlaylistClicked(playlist)
        }

        recyclerView.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNullOrEmpty()) {
                placeholderLayout.visibility = View.VISIBLE
                scrollView.visibility = View.GONE
            } else {
                placeholderLayout.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                adapter.submitList(playlists)
            }
        }

        view.findViewById<Button>(R.id.createPlaylist).setOnClickListener {
            viewModel.onCreatePlaylistClicked()
        }

        viewModel.navigateToCreate.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                navController.navigate(R.id.playlistCreatorFragment)
                viewModel.onNavigationHandled()
            }
        }
    }
    private fun onPlaylistClicked(playlist: PlaylistUi) {
        // Навигация на фрагмент просмотра плейлиста
        val action = MediaFragmentDirections
            .actionPlaylistsFragmentToPlaylistDetailFragment(playlist.id)
        navController.navigate(action)
    }
}