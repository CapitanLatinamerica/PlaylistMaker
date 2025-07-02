package com.practicum.playlistmaker.media.fragmentes.creator.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.practicum.playlistmaker.media.fragmentes.creator.viewmodel.PlaylistCreatorViewModel

class PlaylistCreatorFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistCreatorFragment()
    }

    private lateinit var viewModel: PlaylistCreatorViewModel
    private lateinit var binding: FragmentPlaylistCreatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

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

        val nameField = binding.newPlaylistEditName
        val descriptionField = binding.playlistDescriptionEditText

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        nameField.addTextChangedListener(watcher)
        descriptionField.addTextChangedListener(watcher)
    }


    private fun updateButtonState() {
        val name = binding.newPlaylistEditName.text.toString().isNotEmpty()
        val description = binding.playlistDescriptionEditText.text.toString().isNotEmpty()
        binding.createButton.isEnabled = name && description
    }
}

