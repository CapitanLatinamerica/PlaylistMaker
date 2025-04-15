package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnFind).setOnClickListener {
            viewModel.onSearchClicked()
        }

        findViewById<Button>(R.id.btnMedia).setOnClickListener {
            viewModel.onMediaClicked()
        }

        findViewById<Button>(R.id.btnAdjusts).setOnClickListener {
            viewModel.onSettingsClicked()
        }
    }
}