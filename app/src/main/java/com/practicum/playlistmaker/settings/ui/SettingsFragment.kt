package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
        }

        // Находим Switch для темы
        val themeSwitch = view.findViewById<SwitchMaterial>(R.id.switch1)

        // Подписываемся на изменения темы
        viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDark ->
            themeSwitch.isChecked = isDark
        }

        // Обработчик переключения темы
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }

        // Обработчики кнопок
        view.findViewById<TextView>(R.id.button_share_app).setOnClickListener {
            viewModel.shareApp(requireContext())
        }

        view.findViewById<TextView>(R.id.button_help).setOnClickListener {
            viewModel.contactSupport(requireContext())
        }

        view.findViewById<TextView>(R.id.button_licence).setOnClickListener {
            viewModel.openTerms(requireContext())
        }
    }
}