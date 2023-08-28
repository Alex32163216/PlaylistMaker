package com.example.playlistmaker.utils

import android.media.MediaPlayer
import com.example.playlistmaker.Track
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.presentation.player.PlayerPresenter

object Creator {

    private fun providePlayerInteractor(
        track: Track,
        mediaPlayer: MediaPlayer,
    ): PlayerInteractor {
        return PlayerInteractorImpl(track, mediaPlayer)
    }

    fun providePlayerPresenter(track: Track, mediaPlayer: MediaPlayer): PlayerPresenter {
        return PlayerPresenter(providePlayerInteractor(track, mediaPlayer), track)
    }
}