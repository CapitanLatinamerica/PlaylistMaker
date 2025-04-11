package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val themeSwitch: SwitchMaterial = findViewById(R.id.switch1)

        // Подписка на изменения темы
        viewModel.isDarkTheme.observe(this) { isDark ->
            themeSwitch.isChecked = isDark
        }

        toolbar.setNavigationOnClickListener { finish() }

        // Переключаем тему через ViewModel
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }

        // Добавляем обработчики для кнопок
        findViewById<TextView>(R.id.button_share_app).setOnClickListener {
            viewModel.shareApp(this@SettingsActivity)
        }

        findViewById<TextView>(R.id.button_help).setOnClickListener {
            viewModel.contactSupport(this@SettingsActivity)
        }

        findViewById<TextView>(R.id.button_licence).setOnClickListener {
            viewModel.openTerms(this@SettingsActivity)
        }
    }
}
