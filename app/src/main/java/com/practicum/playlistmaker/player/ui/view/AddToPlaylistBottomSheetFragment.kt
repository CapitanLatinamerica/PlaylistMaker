package com.practicum.playlistmaker.player.ui.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragments.playlists.ui.AddToPlaylistAdapter
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi

class AddToPlaylistBottomSheetFragment : BottomSheetDialogFragment() {

    interface Listener {
        fun onPlaylistClicked(playlist: PlaylistUi)
        fun onNewPlaylistClicked()
    }

    private var listener: Listener? = null
    private lateinit var adapter: AddToPlaylistAdapter
    private val playlists = mutableListOf<PlaylistUi>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw IllegalStateException("Parent must implement Listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_to_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.playlists_recycler)
        val newButton = view.findViewById<TextView>(R.id.btn_new_playlist)

        adapter = AddToPlaylistAdapter { playlist ->
            listener?.onPlaylistClicked(playlist)
            dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        adapter.submitList(playlists)

        newButton.setOnClickListener {
            dismiss()
            listener?.onNewPlaylistClicked()
        }
    }

    fun updatePlaylists(newList: List<PlaylistUi>) {
        playlists.clear()
        playlists.addAll(newList)
        adapter.submitList(playlists.toList()) // ensure new list instance
    }
}
