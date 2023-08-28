package com.example.playlistmaker.domain.api

interface PlayerInteractor {
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerState(): Int
    fun getCurrentTime(): Int
}