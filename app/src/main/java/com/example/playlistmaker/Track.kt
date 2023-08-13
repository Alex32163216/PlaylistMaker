package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlin.collections.ArrayList

data class Track(
    val trackId: Int, //уникальный номер трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Int, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, // Название альбома
    val releaseDate: String, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val previewUrl: String, // Ссылка на отрывок трека
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
        trackTime2.text = Time.millisToStrFormat(model.trackTimeMillis)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.vector9)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.radius)))
            .into(artworkUrl1002)
    }
}

class TracksAdapter(
    private val clickListener: ClickListener,
) : RecyclerView.Adapter<TrackViewer>() {
    lateinit var track : ArrayList<Track>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewer {
        return TrackViewer(parent)

    }

    override fun onBindViewHolder(viewer: TrackViewer, position: Int) {
        viewer.bind(track[position])

        viewer.itemView.setOnClickListener{ clickListener.click(track[position])}
    }

    override fun getItemCount(): Int = track.size
    fun interface ClickListener {
        fun click(track: Track)
    }

}