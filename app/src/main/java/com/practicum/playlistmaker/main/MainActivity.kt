package com.practicum.playlistmaker.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.view.MediaActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonFind = findViewById<Button>(R.id.btnFind)
        val buttonMedia = findViewById<Button>(R.id.btnMedia)
        val buttonSettings = findViewById<Button>(R.id.btnAdjusts)

        buttonFind.setOnClickListener {
            val displayFindActivity = Intent(this, SearchActivity::class.java)
            startActivity(displayFindActivity)
        }

        buttonMedia.setOnClickListener {
            val displayMediaActivity = Intent(this, MediaActivity::class.java)
            startActivity(displayMediaActivity)
        }

        buttonSettings.setOnClickListener {
            val displaySettingsActivity = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettingsActivity)
        }

    }
}