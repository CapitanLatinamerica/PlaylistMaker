package com.practicum.playlistmaker.media.fragments.creator.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.media.fragments.creator.viewmodel.PlaylistCreatorViewModel
import com.practicum.playlistmaker.navigation.NavigationGuard
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreatorFragment : Fragment(), NavigationGuard {

    companion object {
        fun newInstance() = PlaylistCreatorFragment()
    }

    private val viewModel: PlaylistCreatorViewModel by viewModel()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentPlaylistCreatorBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                imageUri?.let {
                    selectedImageUri = it
                    binding.poster.setImageURI(it)
                    // Убираем фон, если изображение выбрано
                    binding.poster.background = null
                }
            }
        }

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

        binding.poster.setOnClickListener {
            openImagePicker()
        }

        binding.createButton.setOnClickListener {
            val name = binding.newPlaylistEditName.text.toString()
            val description = binding.playlistDescriptionEditText.text.toString()
            val coverUri = selectedImageUri?.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.createPlaylist(
                    name = name,
                    description = description,
                    coverPath = coverUri
                )
            }

            Toast.makeText(requireContext(), "Плейлист создан", Toast.LENGTH_SHORT).show()

        }
    }


    private fun updateButtonState() {
        val name = binding.newPlaylistEditName.text.toString().isNotEmpty()
        val description = binding.playlistDescriptionEditText.text.toString().isNotEmpty()
        binding.createButton.isEnabled = name && description
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext(), R.style.MyAwesomeAlertDialogTheme)

            .setTitle(R.string.alert_dialog_title)
            .setMessage(R.string.alert_dialog_message)
            .setPositiveButton(R.string.alert_dialog_positive) { _, _ ->
                viewModel.confirmExit()
            }
            .setNegativeButton(R.string.alert_dialog_negative, null)
            .show()
    }
    override fun shouldBlockNavigation(): Boolean {
        val name = binding.newPlaylistEditName.text.toString()
        val description = binding.playlistDescriptionEditText.text.toString()
        val imageSet = viewModel.isImageSelected()

        return name.isNotBlank() || description.isNotBlank() || imageSet
    }

    override fun requestExitConfirmation(onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext(), R.style.MyAwesomeAlertDialogTheme)
            .setTitle(R.string.alert_dialog_title)
            .setMessage(R.string.alert_dialog_message)
            .setPositiveButton(R.string.alert_dialog_positive) { _, _ ->
                viewModel.confirmExit()
            }
            .setNegativeButton(R.string.alert_dialog_negative, null)
            .show()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }

        val chooser = Intent.createChooser(intent, getString(R.string.choose_image_from))
        pickImageLauncher.launch(chooser)
    }
}

