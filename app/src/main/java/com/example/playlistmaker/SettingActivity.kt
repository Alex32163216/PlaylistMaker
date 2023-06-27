package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

//-----Кнопка возврата------//
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.back).setNavigationOnClickListener {
            finish()
        }

//-----Поделиться приложением------//
        findViewById<TextView>(R.id.share).setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_ink))
                type = "text/plain"
                startActivity(this)
            }
        }

//-----Написать в поддержку------//
        findViewById<TextView>(R.id.write_to_support).setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_e_mail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.massage_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.message_text))
                startActivity(this)
            }
        }

//-----Пользовательское соглашение------//
        findViewById<TextView>(R.id.terms_of_use).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.web_page_links))))
        }

//-----Перекл. черного/анти -плаща ------//
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.dark_theme)
        themeSwitcher.isChecked = (applicationContext as Additionally).darkwingDuck
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as Additionally).switchTheme(checked)
        }

    }

}