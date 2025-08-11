package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi
import com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel.MyAwesomePlaylistFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyAwesomePlaylistFragment : Fragment() {

    private val viewModel: MyAwesomePlaylistFragmentViewModel by viewModel()
    private val args: MyAwesomePlaylistFragmentArgs by navArgs()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Получаем ссылку на NavController для навигации
        navController = NavHostFragment.findNavController(this)
        // Получаем корневой вид и скрываем нижнюю панель навигации
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE

        return inflater.inflate(R.layout.fragment_playlist_awesome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Я покрашу иконку прямо здесь, чтобы не создавать копию
        val btnShare = view?.findViewById<ImageView>(R.id.btnShare)
        btnShare?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yp_black), PorterDuff.Mode.SRC_IN)

        // Получаем playlistId из аргументов
        val playlistId = args.playlistId
        // Загружаем данные о плейлисте через ViewModel
        viewModel.loadPlaylistDetails(playlistId)

        // Подключаем данные к UI
        viewModel.playlistDetails.observe(viewLifecycleOwner) { playlist ->
            // Отображаем детали плейлиста
            updateUI(playlist)
        }

        // Настроим навигационную кнопку назад
        val toolbar = view?.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.headerToolbar)
        toolbar?.setNavigationOnClickListener {
            navController.navigateUp()
        }
    }

    private fun updateUI(playlist: PlaylistUi) {
        // Обновляем текстовые поля
        view?.findViewById<TextView>(R.id.titleText)?.text = playlist.name
        view?.findViewById<TextView>(R.id.subtitleText)?.text = playlist.description

        // Обновляем изображение
        val coverImage = view?.findViewById<ImageView>(R.id.coverImage)
        Glide.with(this)
            .load(playlist.coverPath)
            .placeholder(R.drawable.playlist_cover_placeholder)
            .into(coverImage?: return)

        // Заполняем другие данные
        view?.findViewById<TextView>(R.id.durationLabel)?.text = "${playlist.trackCount} tracks"
    }

    // Показываем нижнюю панель навигации при закрытии фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            MyAwesomePlaylistFragment().apply {
            }
    }
}