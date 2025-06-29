package com.practicum.playlistmaker.media.fragmentes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.db.presentation.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.TrackAdapter
import com.practicum.playlistmaker.player.data.Constants
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.player.ui.view.AudioPlayerActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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

        setupRecyclerView()

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

    private fun setupRecyclerView() {
        adapter = TrackAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickListener { track ->
            openAudioPlayer(track)
        }
    }

    private fun openAudioPlayer(track: Track) {
        startActivity(Intent(requireContext(), AudioPlayerActivity::class.java).apply {
            putExtra(Constants.Extra.TRACK_ID, track.trackId)
            putExtra(Constants.Extra.TRACK_NAME, track.trackName)
            putExtra(Constants.Extra.ARTIST_NAME, track.artistName)
            putExtra(Constants.Extra.TRACK_TIME, track.trackTimeMillis.toString())
            putExtra(Constants.Extra.ALBUM_COVER, track.getArtworkUrl512())
            putExtra(Constants.Extra.COLLECTION_NAME, track.collectionName ?: "")
            putExtra(Constants.Extra.RELEASE_YEAR, track.releaseDate?.takeIf { it.isNotEmpty() }?.split("-")?.get(0) ?: "")
            putExtra(Constants.Extra.GENRE, track.primaryGenreName ?: "")
            putExtra(Constants.Extra.COUNTRY, track.country ?: "")
            putExtra(Constants.Extra.PREVIEW_URL, track.previewUrl)
            putExtra(Constants.Extra.IS_FAVORITE, track.isFavorite)
            putExtra(Constants.Extra.LOCAL_ID, track.addedAt)
        })
    }
}

