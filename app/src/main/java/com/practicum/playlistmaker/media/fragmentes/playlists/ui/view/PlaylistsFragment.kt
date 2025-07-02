package com.practicum.playlistmaker.media.fragmentes.playlists.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragmentes.playlists.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

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

        viewModel.navigateToCreate.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                navController.navigate(R.id.playlistCreatorFragment)
                viewModel.onNavigationHandled()
            }
        }
    }
}