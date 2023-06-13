package com.example.playlistmaker


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<Button>(R.id.search)
        search.setOnClickListener { val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        val media = findViewById<Button>(R.id.media)
        media.setOnClickListener { val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val setting = findViewById<Button>(R.id.setting)
        setting.setOnClickListener { val settingIntent = Intent(this, SettingActivity::class.java)
            startActivity(settingIntent)
        }
    }
}