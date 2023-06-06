package com.example.playlistmaker

import android.content.Intent
import android.widget.ImageView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class SettingActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrowBack = findViewById<ImageView>(R.id.setting2)
        arrowBack.setOnClickListener {
            finish()
        }
    }
}