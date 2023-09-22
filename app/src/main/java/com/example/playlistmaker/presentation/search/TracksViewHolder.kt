package com.example.playlistmaker.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.Time

class TracksViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context).inflate(R.layout.track, parentView, false)
) {

    private val trackName: TextView = itemView.findViewById(R.id.tName)
    private val artistName: TextView = itemView.findViewById(R.id.aName)
    private val trackTime: TextView = itemView.findViewById(R.id.tTime)
    private val artwork: ImageView = itemView.findViewById(R.id.aUrl100)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = Time.millisToStrFormat(model.trackTimeMillis)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.vector9)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.radius)))
            .into(artwork)
    }
}