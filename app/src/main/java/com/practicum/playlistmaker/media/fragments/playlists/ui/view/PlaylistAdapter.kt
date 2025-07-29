/*package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi

class PlaylistsAdapter(
    private val playlists: List<PlaylistUi>,
    private val onClick: (PlaylistUi) -> Unit,
    private val items: MutableList<PlaylistUi> = mutableListOf<PlaylistUi>()
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.itemImagePL)
        private val name: TextView = itemView.findViewById(R.id.itemNamePL)
        private val count: TextView = itemView.findViewById(R.id.itemCountPL)

        fun bind(item: PlaylistUi) {
            name.text = item.name
            count.text = itemView.context.resources.getQuantityString(
                R.plurals.track_count,
                item.trackCount,
                item.trackCount
            )

            Glide.with(itemView)
                .load(item.coverPath)
                .placeholder(R.drawable.new_pl_image_placeholder)
                .centerCrop()
                .into(image)

            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    fun submitList(newList: List<PlaylistUi>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}*/


package com.practicum.playlistmaker.media.fragments.playlists.ui.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.fragments.playlists.ui.PlaylistUi

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val items = mutableListOf<PlaylistUi>()

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
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.itemNamePL)
        private val descriptionText: TextView = itemView.findViewById(R.id.itemCountPL)
        private val coverImage: ImageView = itemView.findViewById(R.id.itemImagePL)

        fun bind(playlist: PlaylistUi) {
            titleText.text = playlist.name
            descriptionText.text = "${playlist.trackCount} треков"

            if (!playlist.coverPath.isNullOrEmpty()) {
                coverImage.setImageURI(Uri.parse(playlist.coverPath))
            } else {
                coverImage.setImageResource(R.drawable.new_pl_image_placeholder)
            }
        }
    }
}

