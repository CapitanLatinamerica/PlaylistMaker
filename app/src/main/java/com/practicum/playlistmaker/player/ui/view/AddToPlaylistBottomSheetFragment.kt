package com.practicum.playlistmaker.player.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragments.creator.view.PlaylistCreatorFragment
import com.practicum.playlistmaker.media.fragments.playlists.ui.view.AddToPlaylistAdapter
import com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel.AddToPlaylistViewModel
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddToPlaylistBottomSheetFragment : BottomSheetDialogFragment() {

    // Получаем Track
    private lateinit var track: Track

    private val viewModel: AddToPlaylistViewModel by viewModel {
        parametersOf(track)
    }


    private lateinit var adapter: AddToPlaylistAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var createPlaylistBtn: Button
    private var isTrackAdding = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем track из аргументов (предположим, ты сериализуешь его через Bundle)
        track = requireArguments().getParcelable(ARG_TRACK)
            ?: throw IllegalArgumentException("Track must be provided")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_add_to_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AddToPlaylistAdapter { playlist ->

            if (isTrackAdding) return@AddToPlaylistAdapter
            isTrackAdding = true

            viewModel.addTrackToPlaylist(playlist.id) { success ->
                val msg = if (success) "Трек добавлен в плейлист ${playlist.name}" else "Трек уже есть в плейлисте ${playlist.name}"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                if (success) dismiss()
                isTrackAdding = false
            }
        }

        recyclerView = view.findViewById(R.id.recycler_add_to_playlist)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        createPlaylistBtn = view.findViewById(R.id.button_new_playlist)
        createPlaylistBtn.setOnClickListener {
            track.let { trackToAdd ->
                // Используем родительский FragmentManager для открытия PlaylistCreatorFragment
                PlaylistCreatorFragment.newInstance(trackToAdd)
                    .show(parentFragmentManager, "playlist_creator")
            }
            dismiss()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlists.collectLatest { playlists ->
                adapter.submitList(playlists)
            }
        }

        viewModel.loadPlaylists()
    }

    companion object {
        const val TAG = "AddToPlaylistBottomSheetFragment"
        private const val ARG_TRACK = "arg_track"

        fun newInstance(track: Track?): AddToPlaylistBottomSheetFragment {
            return AddToPlaylistBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TRACK, track)
                }
            }
        }
    }
}
