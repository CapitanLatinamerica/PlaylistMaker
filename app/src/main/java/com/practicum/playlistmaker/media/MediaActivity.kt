package com.practicum.playlistmaker.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaActivity : AppCompatActivity() {

    private val viewModel: MediaViewModel by viewModel { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_media)
        toolbar.setNavigationOnClickListener { finish() }

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        // Получаем адаптер из ViewModel
        viewPager.adapter = viewModel.getAdapter()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.favorite_tracks)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }.attach()
    }
}