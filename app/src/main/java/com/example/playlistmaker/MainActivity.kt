package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val search=findViewById<Button>(R.id.search)
        val media=findViewById<Button>(R.id.media)
        val setting=findViewById<Button>(R.id.setting)
        val searchIntent=Intent(this, SearchActivity::class.java)
        val searchClickListener:View.OnClickListener = object:View.OnClickListener{
            override fun onClick(v:View?) {
                startActivity(searchIntent)
            }
        }
        search.setOnClickListener(searchClickListener)


        media.setOnClickListener{val mediaIntent=Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }


        setting.setOnClickListener{val settingIntent=Intent(this, SettingActivity::class.java)
            startActivity(settingIntent)
        }
    }
}