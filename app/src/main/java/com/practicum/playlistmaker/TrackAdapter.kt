package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Адаптер для отображения списка треков
class TrackAdapter(private val tracks: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    // Создание и возврат ViewHolder для каждого элемента списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    // Привязка данных к элементам интерфейса
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]

        // Устанавливаем данные в элементы интерфейса
        holder.trackNameTextView.text = track.trackName
        holder.artistNameTextView.text = track.artistName
        holder.trackTimeTextView.text = track.trackTime

        // Загружаем изображение с помощью Glide
        Glide.with(holder.artworkImageView.context)
            .load(track.artworkUrl100)
            .placeholder(R.mipmap.place_holder)
            .error(R.drawable.error_image)  // Изображение ошибки
            .into(holder.artworkImageView)

    }

    // Возвращаем размер списка
    override fun getItemCount(): Int {
        return tracks.size
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)
        val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
        val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)
    }
}
