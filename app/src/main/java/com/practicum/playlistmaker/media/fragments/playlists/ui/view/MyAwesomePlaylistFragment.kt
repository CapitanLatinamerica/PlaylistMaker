package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.app.AlertDialog
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
import com.practicum.playlistmaker.util.showDeleteTrackDialog

class MyAwesomePlaylistFragment : Fragment() {

    private lateinit var trackAdapter: TrackAdapter
    private val viewModel: MyAwesomePlaylistFragmentViewModel by viewModel()
    private val args: MyAwesomePlaylistFragmentArgs by navArgs()
    private lateinit var navController: NavController
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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

        initNavController()
        hideBottomNavigation()
        initRecyclerView(view)
        initBottomSheet(view)
        setupClickListeners(view)
        observeViewModel()
        loadPlaylist()
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

    private fun showDeleteTrackDialog(track: Track) {
        showDeleteTrackDialog(track.trackName) {
            viewModel.deleteTrackFromPlaylist(track.trackId.toLong())
        }
    }

    private fun initNavController() {
        navController = NavHostFragment.findNavController(this)
    }

    private fun hideBottomNavigation() {
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
    }

    private fun initRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.listTracks)
        val emptyStateView = view.findViewById<TextView>(R.id.itHasNotTracks)

        trackAdapter = TrackAdapter(mutableListOf())
        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        trackAdapter.setOnItemClickListener { track ->
            openAudioPlayer(track)
        }
        trackAdapter.setOnItemLongClickListener { track ->
            showDeleteTrackDialog(track)
        }

        // Подписка на изменения списка треков
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tracksStateFlow.collect { tracks ->
                if (tracks.isEmpty()) {
                    emptyStateView.visibility = View.VISIBLE
                } else {
                    emptyStateView.visibility = View.GONE
                }
                trackAdapter.submitList(tracks)

                updateDurationAndTrackCount(tracks, view)
            }
        }
    }

    private fun updateDurationAndTrackCount(tracks: List<Track>, view: View) {
        val totalDurationMillis = tracks.sumOf { it.trackTimeMillis.toLong() }
        val totalDurationMinutes = (totalDurationMillis / 1000 / 60).toInt()
        val minutesText = resources.getQuantityString(R.plurals.playlist_minutes, totalDurationMinutes, totalDurationMinutes)

        view.findViewById<TextView>(R.id.durationLabel)?.text = minutesText
        view.findViewById<TextView>(R.id.trackCountLabel)?.text = getTrackCountText(tracks.size)
    }

    private fun initBottomSheet(view: View) {
        val menuSheet = view.findViewById<LinearLayout>(R.id.menuSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(menuSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        menuSheet.visibility = View.GONE

        val btnMenu = view.findViewById<ImageView>(R.id.btnMenu)
        btnMenu.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                menuSheet.visibility = View.VISIBLE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    menuSheet.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        setupBottomSheetMenuActions(view)
    }

    private fun setupBottomSheetMenuActions(view: View) {
        val shareAction = view.findViewById<TextView>(R.id.actionShare)
        shareAction.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            shareCurrentPlaylist()
        }

        val deleteAction = view.findViewById<TextView>(R.id.actionDelete)
        deleteAction.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            confirmDeletePlaylist()
        }

        val editAction = view.findViewById<TextView>(R.id.actionEdit)
        editAction.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            navigateToEditPlaylist()
        }
    }

    private fun shareCurrentPlaylist() {
        val playlist = viewModel.playlistDetails.value ?: return
        val tracks = viewModel.tracksStateFlow.value

        if (tracks.isNullOrEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.no_tracks_to_share_message), Toast.LENGTH_SHORT).show()
        } else {
            sharePlaylist(playlist, tracks)
        }
    }

    private fun confirmDeletePlaylist() {
        val playlist = viewModel.playlistDetails.value ?: return

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

    private fun navigateToEditPlaylist() {
        val playlist = viewModel.playlistDetails.value ?: return

        val action = MyAwesomePlaylistFragmentDirections.actionMyAwesomePlaylistFragmentToPlaylistCreatorFragmentForEdit(
            argIsEdit = true,
            playlistId = playlist.id,
            playlistName = playlist.name,
            playlistDescription = playlist.description ?: "",
            playlistCoverPath = playlist.coverPath ?: ""
        )
        navController.navigate(action)
    }

    private fun setupClickListeners(view: View) {
        val btnShare = view.findViewById<ImageView>(R.id.btnShare)
        btnShare?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yp_black), PorterDuff.Mode.SRC_IN)
        btnShare?.setOnClickListener {
            shareCurrentPlaylist()
        }

        val toolbar = view.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.headerToolbar)
        toolbar?.setNavigationOnClickListener {
            navController.navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.playlistDetails.observe(viewLifecycleOwner) { playlist ->
            updateUI(playlist)
        }
    }

    private fun loadPlaylist() {
        val playlistId = args.playlistId
        viewModel.loadPlaylistDetails(playlistId)
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