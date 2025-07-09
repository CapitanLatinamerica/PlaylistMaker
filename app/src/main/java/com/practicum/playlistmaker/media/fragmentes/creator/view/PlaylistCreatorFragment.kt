package com.practicum.playlistmaker.media.fragmentes.creator.view

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.media.fragmentes.creator.viewmodel.PlaylistCreatorViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreatorFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistCreatorFragment()
    }

    private val viewModel: PlaylistCreatorViewModel by viewModel()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentPlaylistCreatorBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val nameField = binding.newPlaylistEditName
        val descriptionField = binding.playlistDescriptionEditText

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        // Назад по кнопке в тулбаре
        binding.toolbar.setNavigationOnClickListener {
            viewModel.onBackPressed(
                title = nameField.text.toString(),
                description = descriptionField.text.toString(),
                isImageSet = viewModel.isImageSelected()
            )
        }

        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) { shouldClose ->
            if (shouldClose) {
                navController.popBackStack()
            }
        }

        nameField.addTextChangedListener(watcher)
        descriptionField.addTextChangedListener(watcher)

        viewModel.showExitDialog.observe(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

    }


    private fun updateButtonState() {
        val name = binding.newPlaylistEditName.text.toString().isNotEmpty()
        val description = binding.playlistDescriptionEditText.text.toString().isNotEmpty()
        binding.createButton.isEnabled = name && description
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext(), R.style.MyAwesomeAlertDialogTheme)

            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Завершить") { _, _ ->
                viewModel.confirmExit()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

}

