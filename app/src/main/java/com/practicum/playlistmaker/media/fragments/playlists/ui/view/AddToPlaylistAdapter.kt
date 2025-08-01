package com.practicum.playlistmaker.media.fragments.playlists.ui.view

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

class AddToPlaylistAdapter(
    private val onClick: (PlaylistUi) -> Unit
) : RecyclerView.Adapter<AddToPlaylistAdapter.PlaylistViewHolder>() {

    private val items = mutableListOf<PlaylistUi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_to_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newList: List<PlaylistUi>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cover: ImageView = itemView.findViewById(R.id.ivCover)
        private val name: TextView = itemView.findViewById(R.id.tvName)
        private val count: TextView = itemView.findViewById(R.id.tvCount)

        fun bind(playlist: PlaylistUi) {
            name.text = playlist.name
            count.text = getTrackCountText(playlist.trackCount)

            Glide.with(itemView)
                .load(playlist.coverPath)
                .placeholder(R.drawable.playlist_cover_placeholder)
                .centerCrop()
                .into(cover)

            itemView.setOnClickListener {
                onClick(playlist)
            }
        }
    }
}