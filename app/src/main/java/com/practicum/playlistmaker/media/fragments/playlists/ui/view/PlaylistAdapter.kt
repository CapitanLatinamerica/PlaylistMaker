package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi
import com.practicum.playlistmaker.util.getTrackCountText

class PlaylistAdapter (
    private val onPlaylistClicked: (PlaylistUi) -> Unit,
    private var playlists: List<PlaylistUi>
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val items = mutableListOf<PlaylistUi>()
    private var onItemClickListener: ((PlaylistUi) -> Unit)? = null
    private var onItemLongClickListener: ((PlaylistUi) -> Unit)? = null


    fun setOnItemClickListener(listener: (PlaylistUi) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (PlaylistUi) -> Unit) {
        onItemLongClickListener = listener
    }

    fun submitList(newList: List<PlaylistUi>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {

        val playlist = items[position]
        holder.bind(playlist)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(playlist)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.invoke(playlist)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.itemNamePL)
        private val descriptionText: TextView = itemView.findViewById(R.id.itemCountPL)
        private val coverImage: ImageView = itemView.findViewById(R.id.itemImagePL)

        fun bind(playlist: PlaylistUi) {
            titleText.text = playlist.name
            descriptionText.text = getTrackCountText(playlist.trackCount)

            if (!playlist.coverPath.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(Uri.parse(playlist.coverPath))
                    .placeholder(R.drawable.playlist_cover_placeholder)
                    .centerCrop()
                    .into(coverImage)
            } else {
                coverImage.setImageResource(R.drawable.playlist_cover_placeholder)
            }

            // Обработка клика на элемент
            itemView.setOnClickListener {
                onPlaylistClicked(playlist) // передаем данные в onPlaylistClicked
            }
        }
    }

    fun updatePlaylists(newPlaylists: List<PlaylistUi>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}

