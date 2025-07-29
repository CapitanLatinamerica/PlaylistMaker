package com.practicum.playlistmaker.media.fragments.playlists.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R

class AddToPlaylistAdapter(
    private val onClick: (PlaylistUi) -> Unit
) : RecyclerView.Adapter<AddToPlaylistAdapter.PlaylistViewHolder>() {

    private val items = mutableListOf<PlaylistUi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom, parent, false)
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
        private val cover: ImageView = itemView.findViewById(R.id.itemImagePL)
        private val name: TextView = itemView.findViewById(R.id.itemNamePL)
        private val count: TextView = itemView.findViewById(R.id.itemCountPL)

        fun bind(item: PlaylistUi) {
            name.text = item.name
            count.text = "${item.trackCount} треков"

            Glide.with(itemView)
                .load(item.coverPath)
                .placeholder(R.drawable.playlist_cover_placeholder)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(4)))
                .into(cover)

            itemView.setOnClickListener {
                onClick(item)
            }
        }

        private fun dpToPx(dp: Int): Int {
            val scale = itemView.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }
    }
}
