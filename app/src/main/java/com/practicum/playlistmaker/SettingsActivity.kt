package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)

        val themeSwitch: SwitchMaterial = findViewById(R.id.switch1)
        // Установка текущего состояния переключателя
        val app = application as App
        themeSwitch.isChecked = app.isDarkThemeEnabled()

        val shareButton: TextView = findViewById(R.id.button_share_app)
        shareButton.setOnClickListener {
            shareApp()
        }
        val supportButton: TextView = findViewById(R.id.button_help)
        supportButton.setOnClickListener {
            sendSupportMail()
        }
        val userAgreementButton: TextView = findViewById(R.id.button_licence)
        userAgreementButton.setOnClickListener {
            openUserLicence()
        }


        toolbar.setNavigationOnClickListener {
            onBackPressed()  // Возвращает на предыдущую страницу
        }


        themeSwitch.setOnCheckedChangeListener { switcher, isChecked: Boolean ->
            app.switchTheme(isChecked)
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_text)
            )
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    private fun sendSupportMail() {
        val studentEmail = getString(R.string.support_manager) // Адресат
        val subject = getString(R.string.support_subject) // Тема письма
        val message = getString(R.string.support_text) // Текст письма

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            // Устанавливаем адрес для отправки письма
            data = Uri.parse("mailto:") // "mailto:" без конкретного адреса
            putExtra(Intent.EXTRA_EMAIL, arrayOf(studentEmail)) // Кому
            putExtra(Intent.EXTRA_SUBJECT, subject) // Тема
            putExtra(Intent.EXTRA_TEXT, message) // Текст письма
        }

        startActivity(Intent.createChooser(emailIntent, getString(R.string.btn_help)))
    }


    private fun openUserLicence() {

        val userAgreementUrl = getString(R.string.user_agreement_url)

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userAgreementUrl))

        startActivity(browserIntent)
    }

}