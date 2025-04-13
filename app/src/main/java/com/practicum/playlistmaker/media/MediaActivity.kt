package com.practicum.playlistmaker.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R

class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_media)
        toolbar.setNavigationOnClickListener { finish() }

        // Настройка TabLayout
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        // Добавляем вкладки
        tabLayout.addTab(tabLayout.newTab().setText(R.string.favorite_tracks))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.playlists))

        // Настраиваем адаптер
        viewPager.adapter = MediaPagerAdapter(this)

        // Связываем TabLayout с ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favorite_tracks)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }.attach()
    }
}