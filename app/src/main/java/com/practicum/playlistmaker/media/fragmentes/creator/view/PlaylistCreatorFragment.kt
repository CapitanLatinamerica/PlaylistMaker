package com.practicum.playlistmaker.media.fragmentes.creator.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.media.fragmentes.creator.viewmodel.PlaylistCreatorViewModel
import com.practicum.playlistmaker.navigation.NavigationGuard
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
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

}

