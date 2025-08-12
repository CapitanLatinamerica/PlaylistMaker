package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi
import com.practicum.playlistmaker.media.fragments.playlists.ui.viewmodel.MyAwesomePlaylistFragmentViewModel
import com.practicum.playlistmaker.player.TrackAdapter
import com.practicum.playlistmaker.player.data.PlayerConstants
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.player.ui.view.AudioPlayerActivity
import com.practicum.playlistmaker.util.getTrackCountText
import com.practicum.playlistmaker.util.showDeletePlaylistDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

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
        val btnShare = view.findViewById<ImageView>(R.id.btnShare)
        btnShare?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yp_black), PorterDuff.Mode.SRC_IN)

        // Получаем playlistId из аргументов
        val playlistId = args.playlistId
        // Загружаем данные о плейлисте через ViewModel
        viewModel.loadPlaylistDetails(playlistId)

        // Подключаем данные к UI
        viewModel.playlistDetails.observe(viewLifecycleOwner) { playlist ->
            // Отображаем детали плейлиста
            updateUI(playlist)

            // Обновляем меню bottom sheet с данными плейлиста
            view.let { rootView ->
                val posterImage = rootView.findViewById<ImageView>(R.id.playlistPoster)
                val infoText = rootView.findViewById<TextView>(R.id.playlistInfoText)
                val trackCountText = rootView.findViewById<TextView>(R.id.playlistTrackCount)

                // Загрузка обложки через Glide (если путь не пуст)
                if (!playlist.coverPath.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(playlist.coverPath)
                        .placeholder(R.drawable.playlist_cover_placeholder)
                        .centerCrop()
                        .into(posterImage)
                } else {
                    posterImage.setImageResource(R.drawable.playlist_cover_placeholder)
                }

                // Название плейлиста
                infoText.text = playlist.name

                // Количество треков
                trackCountText.text = getTrackCountText(playlist.trackCount)
            }
        }

        //Вызов меню плейлиста
        val menuSheet = view.findViewById<LinearLayout>(R.id.menuSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(menuSheet)

        // Скрываем меню по умолчанию
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        menuSheet.visibility = View.GONE

        // Показывать меню по нажатию кнопки
        val btnMenu = view.findViewById<ImageView>(R.id.btnMenu)
        btnMenu.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                menuSheet.visibility = View.VISIBLE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        // Контролируем состояние меню
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    menuSheet.visibility = View.GONE
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        // Обработка клика по пункту Поделиться
        val shareAction = view.findViewById<TextView>(R.id.actionShare)
        shareAction.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN   // Скрываем меню
            val playlist = viewModel.playlistDetails.value
            val tracks = viewModel.tracksStateFlow.value
            if (playlist != null) {
                if (tracks.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.no_tracks_to_share_message), Toast.LENGTH_SHORT).show()
                } else {
                    sharePlaylist(playlist, tracks)
                }
            }
        }

        // Обработка клика по пункту Удалить
        val deleteAction = view.findViewById<TextView>(R.id.actionDelete)
        deleteAction.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN  // Скрываем меню

            val playlist = viewModel.playlistDetails.value
            if (playlist == null) {
                Toast.makeText(requireContext(), "Ошибка: плейлист не загружен", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showDeletePlaylistDialog(
                title = getString(R.string.playlist_delete),
                message = getString(R.string.delete_playlist_message, playlist.name),
                positiveText = getString(R.string.delete),
                negativeText = getString(R.string.alert_dialog_negative)
            ) {
                viewModel.deletePlaylist(playlist.id)
                navController.popBackStack()
            }
        }

        val editAction = view.findViewById<TextView>(R.id.actionEdit)
        editAction.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            val playlist = viewModel.playlistDetails.value
            if (playlist == null) {
                Toast.makeText(requireContext(), "Плейлист не загружен", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val action = MyAwesomePlaylistFragmentDirections.actionMyAwesomePlaylistFragmentToPlaylistCreatorFragmentForEdit(
                playlistId = playlist.id,
                playlistName = playlist.name,
                playlistDescription = playlist.description ?: "",
                playlistCoverPath = playlist.coverPath ?: ""
            )
            navController.navigate(action)

        }


        // Кнопка share
        btnShare?.setOnClickListener {
            val playlist = viewModel.playlistDetails.value
            val tracks = viewModel.tracksStateFlow.value

            if (playlist == null) {
                // Можно показать ошибку или ничего не делать
                return@setOnClickListener
            }

            if (tracks.isNullOrEmpty()) {
                // Треков нет — показать предупреждение
                Toast.makeText(requireContext(),
                    getString(R.string.no_tracks_to_share_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sharePlaylist(playlist, tracks)
            }
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

    private fun sharePlaylist(playlist: PlaylistUi, tracks: List<Track>) {
        val shareText = buildShareText(playlist, tracks)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_playlist_title)))
    }

    private fun buildShareText(playlist: PlaylistUi, tracks: List<Track>): String {
        val sb = StringBuilder()
        sb.append(playlist.name).append("\n")
        sb.append(playlist.description).append("\n")
        sb.append("${tracks.size} ${getTrackCountText(tracks.size)}").append("\n")

        tracks.forEachIndexed { index, track ->
            val trackTimeFormatted = com.practicum.playlistmaker.player.ui.TimeFormatter.formatTrackTime(track.trackTimeMillis)
            sb.append("${index + 1}. ${track.artistName} - ${track.trackName} ($trackTimeFormatted)").append("\n")
        }

        return sb.toString().trimEnd()
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