package com.example.playlistmaker

import java.text.SimpleDateFormat
import java.util.*

object Time {
    fun millisToStrFormat(millis: Int) =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
}