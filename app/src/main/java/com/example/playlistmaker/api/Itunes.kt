package com.practicum.playlistmaker.apple

import com.example.playlistmaker.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface Itunes {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}