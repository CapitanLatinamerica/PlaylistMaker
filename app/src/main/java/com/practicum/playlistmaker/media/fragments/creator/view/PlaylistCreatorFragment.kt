package com.practicum.playlistmaker.media.fragments.creator.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.media.fragments.creator.viewmodel.PlaylistCreatorViewModel
import com.practicum.playlistmaker.navigation.NavigationGuard
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistCreatorFragment : DialogFragment(), NavigationGuard {

    private var trackToAdd: Track? = null

    companion object {
        fun newInstance(track: Track?): PlaylistCreatorFragment {
            return PlaylistCreatorFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("track_to_add", track)
                }
            }
        }
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
        trackToAdd = arguments?.getParcelable("track_to_add")

        val nameField = binding.newPlaylistEditName
        val descriptionField = binding.playlistDescriptionEditText

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

        nameField.addTextChangedListener { updateButtonState() }
        descriptionField.addTextChangedListener { updateButtonState() }

        viewModel.showExitDialog.observe(viewLifecycleOwner) {
            showExitDialog()
        }

        binding.poster.setOnClickListener {
            openImagePicker()
        }

        binding.createButton.setOnClickListener {
            val name = binding.newPlaylistEditName.text.toString()
            val description = binding.playlistDescriptionEditText.text.toString()
            val coverUri = selectedImageUri?.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                val newPlaylistId = viewModel.createPlaylist(
                    name = name,
                    description = description,
                    coverPath = coverUri
                )

                val result = Bundle().apply {
                    putParcelable("track_to_add", trackToAdd)
                    putLong("created_playlist_id", newPlaylistId)
                }
                parentFragmentManager.setFragmentResult("playlist_created_result", result)
                // Закрываем текущий фрагмент
                parentFragmentManager.popBackStack()
                dismiss()
            }
        }
    }

    private fun updateButtonState() {
        binding.createButton.isEnabled = binding.newPlaylistEditName.text
            .toString()
            .isNotEmpty() // Кнопка "Создать" будет активна сразу как только текст появится в поле "Название"
    }

    override fun shouldBlockNavigation(): Boolean {
        return binding.newPlaylistEditName.text.toString().isNotBlank()         //Проверяем только наличие текста в поле "Название"
    }

    override fun requestExitConfirmation(onConfirm: () -> Unit) {
        showExitDialog()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }

        val chooser = Intent.createChooser(intent, getString(R.string.choose_image_from))
        pickImageLauncher.launch(chooser)
    }

    private fun showExitDialog(onConfirm: (() -> Unit)? = null) {
        AlertDialog.Builder(requireContext(), R.style.MyAwesomeAlertDialogTheme)
            .setTitle(R.string.alert_dialog_title)
            .setMessage(R.string.alert_dialog_message)
            .setPositiveButton(R.string.alert_dialog_positive) { _, _ ->
                onConfirm?.invoke() ?: viewModel.confirmExit()
            }
            .setNegativeButton(R.string.alert_dialog_negative, null)
            .show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackToAdd = arguments?.getParcelable("track_to_add")
    }
}

