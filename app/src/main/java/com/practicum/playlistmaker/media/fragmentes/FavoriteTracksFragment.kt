package com.practicum.playlistmaker.media.fragmentes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragmentes.viewmodel.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteTracksFragment : Fragment() {

    private val viewModel: FavoriteTracksViewModel by viewModel()


    private lateinit var adapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyPlaceholder: View

    companion object {
        fun newInstance(): FavoriteTracksFragment {
            return FavoriteTracksFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_tracks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_favorites)
        emptyPlaceholder = view.findViewById<View>(R.id.iv_no_results).parent as View

        adapter = TrackAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Наблюдение за StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteTracks.collect { tracks ->
                    if (tracks.isNullOrEmpty()) {
                        emptyPlaceholder.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        emptyPlaceholder.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter.submitList(tracks)
                    }
                }
            }
        }
    }
}

