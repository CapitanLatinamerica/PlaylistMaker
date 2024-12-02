package com.practicum.playlistmaker

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val buttonFind = findViewById<Toolbar>(R.id.toolbar)
 //       val switch1: Switch = findViewById(R.id.switch1)

        buttonFind.setOnClickListener {
            val displayMainActivity = Intent(this, MainActivity::class.java)
            startActivity(displayMainActivity)
        }

 /*       val thumbColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked), // checked
                intArrayOf(-android.R.attr.state_checked)  //unchecked
            ),
            intArrayOf(
                Color.parseColor("#3772E7"),
                Color.parseColor("#9FBBF3")
            )
        )

        DrawableCompat.setTintList(DrawableCompat.wrap(switch1.thumbDrawable), thumbColorStateList)*/
    }

    override fun onStop() {
        super.onStop()
        this.finish()
    }
}