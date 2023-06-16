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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_SEARCH = "REQUEST_SEARCH"
    }

    private var editRequest: String = ""
    private lateinit var enteringSearchQuery: EditText
    private lateinit var clearInputQuery: ImageView
    private val searchWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            clearInputQuery.visibility = clearInputQueryVisibility(s)
            editRequest = s.toString()
            Log.i("i", "Текст запроса - $editRequest")
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(REQUEST_SEARCH, editRequest)
        Log.i("i", "Сохраняем текст - $editRequest")
    }

    override fun onRestoreInstanceState(savingInput: Bundle) {
        super.onRestoreInstanceState(savingInput)
        editRequest = savingInput.getString(REQUEST_SEARCH).toString()
        Log.i("i", "Сохраняем текст при перезапуске - $editRequest")
        enteringSearchQuery.setText(editRequest)
    }

    override fun onCreate(savingInput: Bundle?) {
        super.onCreate(savingInput)
        setContentView(R.layout.activity_search)
        val trackList: List<Track> = listOf(
            Track(
                "Smells Like Teen Spirit", "Nirvana", "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean", "Michael Jackson", "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive", "Bee Gees", "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love", "Led Zeppelin", "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine", "Guns N' Roses", "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
        val RecycleViwer = findViewById<RecyclerView>(R.id.compositionSearch)
        RecycleViwer.layoutManager = LinearLayoutManager(this)
        val trackAdapter = TracksAdapter(trackList)
        RecycleViwer.adapter = trackAdapter

        enteringSearchQuery = findViewById(R.id.enteringSearchQuery)
        enteringSearchQuery.addTextChangedListener(searchWatcher)
        clearInputQuery = findViewById(R.id.windowCleaning)
        clearInputQuery.visibility = clearInputQueryVisibility(enteringSearchQuery.text)
        clearInputQuery.setOnClickListener {
            clearInput()
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.back).setNavigationOnClickListener {
            finish()
        }
    }

    private fun clearInputQueryVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearInput() {
        enteringSearchQuery.setText("")
        val view = this.currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }
}