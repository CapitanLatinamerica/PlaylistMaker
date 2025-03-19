package com.practicum.playlistmaker.presentation

import com.practicum.playlistmaker.domain.Track
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R

// Адаптер для отображения списка треков
class TrackAdapter(private val tracks: MutableList<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var onItemClickListener: ((Track) -> Unit)? = null

    fun setOnItemClickListener(listener: (Track) -> Unit) {
        onItemClickListener = listener
    }

    // Обновление списка треков
    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    // Создание и возврат ViewHolder для каждого элемента списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    // Привязка данных к элементам интерфейса
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)

        // Обработка нажатия
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(track) // Ожидаем обработчик клика из SearchActivity
        }
    }

    // Возвращаем размер списка
    override fun getItemCount(): Int {
        return tracks.size
    }

    // ViewHolder для элемента списка
    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Элементы интерфейса
        private val artworkUrl100: ImageView = itemView.findViewById(R.id.artworkImageView)
        private val trackName: TextView = itemView.findViewById(R.id.trackNameTextView)
        private val artistName: TextView = itemView.findViewById(R.id.artistNameTextView)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTimeTextView)


        // Привязка данных
        fun bind(track: Track) {
            // Устанавливаем текст в TextView
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime

            //Пересчитываю разметку для треков внизу списка
            artistName.requestLayout()
            trackTime.requestLayout()

            // Загружаем изображение с использованием Glide
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.p_holder) // Плейсхолдер при загрузке
                .error(R.drawable.error_image) // Плейсхолдер при ошибке
                .into(artworkUrl100)
        }
    }
}
