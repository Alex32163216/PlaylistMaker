package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.util.Log
import android.view.View
import android.text.Editable
import android.widget.EditText

class SearchActivity:AppCompatActivity()  { companion object { const val REQUEST_SEARCH = "REQUEST_SEARCH"
}
    private var editRequest:String = ""
    private lateinit var enteringSearchQuery:EditText
    private lateinit var clearInputQuery:ImageView
    private val searchWatcher = object:TextWatcher {override fun beforeTextChanged(s:CharSequence?, start:Int, count:Int, after:Int) {
    }
        override fun onTextChanged(s:CharSequence?, start:Int, before:Int, count:Int) {clearInputQuery.visibility = clearInputQueryVisibility(s)
            editRequest = s.toString()
            Log.i("i", "Текст запроса - $editRequest")
        }
        override fun afterTextChanged(s:Editable?) {}
    }
    override fun onSaveInstanceState(outState:Bundle) {super.onSaveInstanceState(outState)
        outState.putString(REQUEST_SEARCH, editRequest)
        Log.i("i", "Сохраняем текст - $editRequest")
    }
    override fun onRestoreInstanceState(savingInput:Bundle) {super.onRestoreInstanceState(savingInput)
        editRequest = savingInput.getString(REQUEST_SEARCH).toString()
        Log.i("i", "Сохраняем текст при перезапуске - $editRequest")
        enteringSearchQuery.setText(editRequest)
    }
    override fun onCreate(savingInput:Bundle?) {
        super.onCreate(savingInput)
        setContentView(R.layout.activity_search)
        enteringSearchQuery = findViewById(R.id.enteringSearchQuery)
        enteringSearchQuery.addTextChangedListener(searchWatcher)
        clearInputQuery = findViewById(R.id.windowCleaning)
        clearInputQuery.visibility = clearInputQueryVisibility(enteringSearchQuery.text)
        clearInputQuery.setOnClickListener{ clearInput  ()
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.back).setOnClickListener { finish()
        }
    }
    private fun clearInputQueryVisibility(s:CharSequence?):Int {return if (s.isNullOrEmpty()) { View.GONE } else { View.VISIBLE
    }
    }
    private fun clearInput() { enteringSearchQuery.setText("")
        val view = this.currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }
}