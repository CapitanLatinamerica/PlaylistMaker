package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val buttonFind = findViewById<ImageButton>(R.id.button_back)

        buttonFind.setOnClickListener {
            val displayMainActivity = Intent(this, MainActivity::class.java)
            startActivity(displayMainActivity)
        }
    }

    override fun onStop() {
        super.onStop()
        this.finish()
    }
}