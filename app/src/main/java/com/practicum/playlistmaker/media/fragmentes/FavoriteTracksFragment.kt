package com.practicum.playlistmaker.media.fragmentes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragmentes.viewmodel.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteTracksFragment : Fragment() {

    private val viewModel: FavoriteTracksViewModel by viewModel { parametersOf(this) }

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
}