package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi
import com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel.MyAwesomePlaylistFragmentViewModel
import com.practicum.playlistmaker.player.TrackAdapter
import com.practicum.playlistmaker.player.data.PlayerConstants
import com.practicum.playlistmaker.player.domain.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.player.ui.view.AudioPlayerActivity

class MyAwesomePlaylistFragment : Fragment() {

    private lateinit var trackAdapter: TrackAdapter
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

        // Инициализация RecyclerView и адаптера
        val recyclerView = view.findViewById<RecyclerView>(R.id.listTracks)
        val emptyStateView = view.findViewById<TextView>(R.id.itHasNotTracks)

        trackAdapter = TrackAdapter(mutableListOf())
        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Подписываем обработчик на клик
        trackAdapter.setOnItemClickListener { track ->
            openAudioPlayer(track)
        }

        // Подписываемся на список треков из ViewModel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tracksStateFlow.collect { tracks ->
                if (tracks.isEmpty()) {
                    emptyStateView.visibility = View.VISIBLE
                } else {
                    emptyStateView.visibility = View.GONE
                }
                trackAdapter.submitList(tracks)
            }
        }

        // Загружаем данные
//        val playlistId = args.playlistId
        viewModel.loadPlaylistDetails(playlistId)
    }

    private fun openAudioPlayer(track: Track) {
        startActivity(Intent(requireContext(), AudioPlayerActivity::class.java).apply {
            putExtra(PlayerConstants.Extra.TRACK_ID, track.trackId)
            putExtra(PlayerConstants.Extra.TRACK_NAME, track.trackName)
            putExtra(PlayerConstants.Extra.ARTIST_NAME, track.artistName)
            putExtra(PlayerConstants.Extra.TRACK_TIME, track.trackTimeMillis.toString())
            putExtra(PlayerConstants.Extra.ALBUM_COVER, track.artworkUrl100)
            putExtra(PlayerConstants.Extra.COLLECTION_NAME, track.collectionName ?: "")
            putExtra(PlayerConstants.Extra.RELEASE_YEAR, track.releaseDate?.takeIf { it.isNotEmpty() }?.split("-")?.get(0) ?: "")
            putExtra(PlayerConstants.Extra.GENRE, track.primaryGenreName ?: "")
            putExtra(PlayerConstants.Extra.COUNTRY, track.country ?: "")
            putExtra(PlayerConstants.Extra.PREVIEW_URL, track.previewUrl)
            putExtra(PlayerConstants.Extra.IS_FAVORITE, track.isFavorite)
            putExtra(PlayerConstants.Extra.LOCAL_ID, track.addedAt)
        })
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