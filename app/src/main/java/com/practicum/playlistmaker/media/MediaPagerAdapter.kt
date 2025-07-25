package com.practicum.playlistmaker.media

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.media.fragments.playlists.ui.view.FavoriteTracksFragment
import com.practicum.playlistmaker.media.fragments.playlists.ui.view.PlaylistsFragment

class MediaPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteTracksFragment.newInstance()
            1 -> PlaylistsFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
