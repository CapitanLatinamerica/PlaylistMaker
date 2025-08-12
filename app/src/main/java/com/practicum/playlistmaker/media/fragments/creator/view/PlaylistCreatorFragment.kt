package com.practicum.playlistmaker.media.fragments.creator.view

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.bundle.bundleOf
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.media.fragments.creator.viewmodel.PlaylistCreatorViewModel
import com.practicum.playlistmaker.navigation.NavigationGuard
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistCreatorFragment : DialogFragment(), NavigationGuard {

    private var trackToAdd: Track? = null
    private var isOpenedAsDialog = false
    private val args: PlaylistCreatorFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Определяем, был ли фрагмент открыт как диалог
        isOpenedAsDialog = arguments?.getBoolean("is_dialog", false) == true
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        trackToAdd = arguments?.getParcelable("track_to_add")
    }

    companion object {
        private const val ARG_IS_EDIT = "arg_is_edit"
        private const val ARG_PLAYLIST_ID = "arg_playlist_id"
        private const val ARG_PLAYLIST_NAME = "arg_playlist_name"
        private const val ARG_PLAYLIST_DESCRIPTION = "arg_playlist_description"
        private const val ARG_PLAYLIST_COVER = "arg_playlist_cover"

        fun newInstanceForEdit(
            id: Int,
            name: String,
            description: String?,
            coverPath: String?
        ): PlaylistCreatorFragment {
            return PlaylistCreatorFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_EDIT, true)
                    putInt(ARG_PLAYLIST_ID, id)
                    putString(ARG_PLAYLIST_NAME, name)
                    putString(ARG_PLAYLIST_DESCRIPTION, description)
                    putString(ARG_PLAYLIST_COVER, coverPath)
                }
            }
        }

        fun newInstance(track: Track?, isDialog: Boolean = false): PlaylistCreatorFragment {
            return PlaylistCreatorFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("track_to_add", track)
                    putBoolean("is_dialog", isDialog)
                }
            }
        }
    }

    private val viewModel: PlaylistCreatorViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistCreatorBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

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

        val isEditMode = args.argIsEdit
        val playlistId = args.playlistId

        if (isEditMode) {
            val name = args.playlistName
            val description = args.playlistDescription
            val cover = args.playlistCoverPath

            // Заполняем поля
            binding.newPlaylistEditName.setText(name)
            binding.playlistDescriptionEditText.setText(description)

            if (cover.isNotEmpty()) {
                selectedImageUri = Uri.parse(cover)
                binding.poster.setImageURI(selectedImageUri)
                binding.poster.background = null
            } else {
                binding.poster.setImageResource(R.drawable.new_pl_image_placeholder)
            }

            // Обновляем заголовок и текст кнопки
            binding.toolbar.title = getString(R.string.edit_playlist_title)
            binding.createButton.text = getString(R.string.save)
        } else {
            // Режим создания
            binding.toolbar.title = getString(R.string.create_playlist)
            binding.createButton.text = getString(R.string.new_pl_create)
            binding.poster.setImageResource(R.drawable.new_pl_image_placeholder)
        }

        updateButtonState()

        trackToAdd = arguments?.getParcelable("track_to_add")

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                imageUri?.let {
                    selectedImageUri = it
                    binding.poster.setImageURI(it)
                    binding.poster.background = null
                }
            }
        }

        val nameField = binding.newPlaylistEditName
        val descriptionField = binding.playlistDescriptionEditText

        binding.toolbar.setNavigationOnClickListener {
            if (isEditMode) {
                // Просто закрываем экран без диалога
                viewModel.confirmExit()
            } else {
                // Вызвать диалог, если это экран создания
                handleBackPressed()
            }
        }

        nameField.addTextChangedListener { updateButtonState() }
        descriptionField.addTextChangedListener { updateButtonState() }

        // Наблюдатель для закрытия диалога
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) { shouldClose ->
            if (shouldClose && isAdded) {  // Добавляем проверку isAdded
                if (isOpenedAsDialog) {
                    dismissAllowingStateLoss()  // Используем этот метод вместо dismiss()
                } else {
                    parentFragmentManager.popBackStack()
                }
                viewModel.resetCloseFlag()  // Сбрасываем флаг после закрытия
            }
        }

        viewModel.showExitDialog.observe(viewLifecycleOwner) {
            showExitDialog()
        }

        binding.poster.setOnClickListener {
            openImagePicker()
        }

        binding.createButton.setOnClickListener {
            val name = binding.newPlaylistEditName.text.toString()
            if (name.isEmpty()) return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch {
                if (isEditMode) {
                    // Обновляем плейлист
                    viewModel.updatePlaylist(
                        id = playlistId,
                        name = name,
                        description = binding.playlistDescriptionEditText.text.toString(),
                        coverPath = selectedImageUri?.toString()
                    )
                } else {
                    val newPlaylistId = viewModel.createPlaylist(
                        name = name,
                        description = binding.playlistDescriptionEditText.text.toString(),
                        coverPath = selectedImageUri?.toString()
                    )

                    withContext(Dispatchers.Main) {
                        parentFragmentManager.setFragmentResult(
                            "playlist_created_result",
                            bundleOf(
                                "track_to_add" to trackToAdd,
                                "created_playlist_id" to newPlaylistId
                            )
                        )

                        if (isOpenedAsDialog) {
                            dismissAllowingStateLoss()
                        } else {
                            parentFragmentManager.popBackStack()
                        }
                        val msg = resources.getString(R.string.create_playlist) +
                                " \"$name\" " +
                                resources.getString(R.string.new_pl_created)
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                // Важные параметры окна
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

                // Разрешаем касания внутри диалога
                clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            }
        }

        return dialog
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

    private fun handleBackPressed() {
        val hasChanges = binding.newPlaylistEditName.text.toString().isNotBlank() ||
                binding.playlistDescriptionEditText.text.toString().isNotBlank() ||
                viewModel.isImageSelected()

        if (hasChanges) {
            showExitDialog {
                viewModel.confirmExit()
            }
        } else {
            viewModel.confirmExit()
        }
    }
}

