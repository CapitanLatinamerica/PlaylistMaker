package com.practicum.playlistmaker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

// Адаптер для отображения списка треков
class TrackAdapter(private val tracks: List<Track>, private val context: Context) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    // Создание и возврат ViewHolder для каждого элемента списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    // Привязка данных к элементам интерфейса
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
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
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L) // Это нужно заменить на track.trackTime

            // Загружаем изображение с проверкой наличия интернета
            val context = itemView.context
            if (isNetworkAvailable(context)) {
                Glide.with(context)
                    .load(track.artworkUrl100)
                    .placeholder(R.drawable.p_holder) // Плейсхолдер при загрузке
                    .error(R.drawable.error_image) // Плейсхолдер при ошибке
                    .into(artworkUrl100)
            } else {
                // Если нет интернета, загружаем только плейсхолдер
                Glide.with(context)
                    .load(R.drawable.p_holder)
                    .into(artworkUrl100)
            }
        }
        // Функция для проверки подключения к интернету
        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            return activeNetwork?.isConnected == true
        }
    }
}
