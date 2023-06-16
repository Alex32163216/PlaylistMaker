package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: String, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
)

class TrackViewer(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context).inflate(R.layout.track, parentView, false)
) {

    private val trackName2: TextView = itemView.findViewById(R.id.tName)
    private val artistName2: TextView = itemView.findViewById(R.id.aName)
    private val trackTime2: TextView = itemView.findViewById(R.id.tTime)
    private val artworkUrl1002: ImageView = itemView.findViewById(R.id.aUrl100)

    fun bind(model: Track) {
        trackName2.text = model.trackName
        artistName2.text = model.artistName
        trackTime2.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.vector9)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.radius)))
            .into(artworkUrl1002)
    }
}

class TracksAdapter(
    private val track: List<Track>
) : RecyclerView.Adapter<TrackViewer>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewer {
        return TrackViewer(parent)

    }

    override fun onBindViewHolder(viewer: TrackViewer, position: Int) {
        viewer.bind(track[position])
    }

    override fun getItemCount(): Int = track.size
}