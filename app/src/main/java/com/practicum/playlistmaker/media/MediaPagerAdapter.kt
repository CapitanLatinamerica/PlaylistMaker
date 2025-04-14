package com.practicum.playlistmaker.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.media.fragmentes.FavoriteTracksFragment
import com.practicum.playlistmaker.media.fragmentes.PlaylistsFragment

class MediaPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteTracksFragment()
            1 -> PlaylistsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}