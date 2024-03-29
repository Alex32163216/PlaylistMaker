package com.example.playlistmaker.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.util.Log
import android.view.View
import com.example.playlistmaker.data.dto.TrackResponse
import android.text.Editable
import com.example.playlistmaker.presentation.app.Application.Companion.APPLICATION
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import android.view.inputmethod.EditorInfo
import com.example.playlistmaker.History
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.TracksAdapter
import com.example.playlistmaker.data.network.Itunes
import com.example.playlistmaker.ui.player.PlayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_SEARCH = "REQUEST_SEARCH"
        const val TRACK = "TRACK"
        private const val CLICK_ITEM_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private enum class StateType {
        CONNECTION_ERROR, NOT_FOUND, SEARCH_RESULT, HISTORY_LIST, SEARCH_PROGRESS
    }

    private var editRequest: String = ""
    private val itunesBaseUrl = "http://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(Itunes::class.java)

    private val searchTrackList = ArrayList<Track>()
    private val trackAdapter = TracksAdapter { clickOnTrack(it) }
    private val historyTrackAdapter = TracksAdapter { clickOnTrack(it) }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { searchTrackList() }

    private lateinit var searchHistory: History
    private lateinit var enteringSearchQuery: EditText
    private lateinit var clearInputQuery: ImageView
    private lateinit var recyclerViewTrack: RecyclerView
    private lateinit var refreshButtPh: Button
    private lateinit var errorTextPh: TextView
    private lateinit var errorIcPh: ImageView
    private lateinit var errorPh: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var titleHistory: TextView
    private lateinit var progressBar: ProgressBar

    private val searchWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            clearInputQuery.visibility = clearInputQueryVisibility(s)
            editRequest = s.toString()
            if (enteringSearchQuery.hasFocus()
                && s.isNullOrEmpty()
                && searchHistory.getList().isNotEmpty()
            ) showState(StateType.HISTORY_LIST)
            //else showState(StateType.SEARCH_RESULT)
            else searchDebounce()

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
        progressBar = findViewById(R.id.progressBar)

        recyclerViewTrack = findViewById(R.id.trackSearch)
        recyclerViewTrack.layoutManager = LinearLayoutManager(this)

        searchHistory = History(getSharedPreferences(APPLICATION, MODE_PRIVATE))

        enteringSearchQuery = findViewById(R.id.enteringSearchQuery)
        enteringSearchQuery.addTextChangedListener(searchWatcher)

        enteringSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrackList()
                true
            }
            false
        }
        enteringSearchQuery.setOnFocusChangeListener { view, hasFocus ->
            if (searchHistory.getList().isNotEmpty() && hasFocus) showState(StateType.HISTORY_LIST)
        }

        clearInputQuery = findViewById(R.id.windowCleaning)
        clearInputQuery.visibility = clearInputQueryVisibility(enteringSearchQuery.text)
        clearInputQuery.setOnClickListener {
            clearInput()
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.back).setNavigationOnClickListener {
            finish()
        }

        refreshButtPh = findViewById(R.id.refresh)
        refreshButtPh.setOnClickListener {
            searchTrackList()
        }

        errorIcPh = findViewById(R.id.error_2)
        errorTextPh = findViewById(R.id.error_3)
        errorPh = findViewById(R.id.error)

        clearHistoryButton = findViewById(R.id.clear_history_butt)
        titleHistory = findViewById(R.id.history_title)

        clearHistoryButton.setOnClickListener {
            searchHistory.clearList()
            showState(StateType.SEARCH_RESULT)
        } //  очистка истории
        enteringSearchQuery.requestFocus() //  фокус на поисковую строку

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
        searchTrackList.clear()
        trackAdapter.notifyDataSetChanged()
        val view = this.currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun showState(stateType: StateType) {
        when (stateType) {
            StateType.CONNECTION_ERROR -> {
                recyclerViewTrack.visibility = View.GONE
                errorPh.visibility = View.VISIBLE
                refreshButtPh.visibility = View.VISIBLE


                errorIcPh.setImageResource(R.drawable.no_network)
                errorTextPh.setText(R.string.ph_no_connection)
                titleHistory.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
            StateType.NOT_FOUND -> {
                recyclerViewTrack.visibility = View.GONE
                errorPh.visibility = View.VISIBLE
                refreshButtPh.visibility = View.GONE
                errorIcPh.setImageResource(R.drawable.nothing_found)
                errorTextPh.setText(R.string.ph_not_found)
                titleHistory.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
            StateType.SEARCH_PROGRESS -> {
                recyclerViewTrack.visibility = View.GONE
                errorPh.visibility = View.GONE
                refreshButtPh.visibility = View.GONE
                titleHistory.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            StateType.SEARCH_RESULT -> {
                trackAdapter.tracks = searchTrackList
                recyclerViewTrack.adapter = trackAdapter
                recyclerViewTrack.visibility = View.VISIBLE
                errorPh.visibility = View.GONE
                refreshButtPh.visibility = View.GONE
                titleHistory.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
            StateType.HISTORY_LIST -> {
                historyTrackAdapter.tracks = searchHistory.getList()
                recyclerViewTrack.adapter = historyTrackAdapter
                recyclerViewTrack.visibility = View.VISIBLE
                errorPh.visibility = View.GONE
                refreshButtPh.visibility = View.GONE
                titleHistory.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun searchTrackList() {
        if (enteringSearchQuery.text.isNotEmpty()) {
            showState(StateType.SEARCH_PROGRESS)
            itunesService.search(enteringSearchQuery.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            searchTrackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                searchTrackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                showState(StateType.SEARCH_RESULT)
                            } else showState(StateType.NOT_FOUND)
                        } else showState(StateType.CONNECTION_ERROR)
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showState(StateType.CONNECTION_ERROR) //показывать плейсхолдер с ошибкой
                    }
                })
        }
    }

    private fun clickOnTrack(track: Track) {
        if (clickDebounce()) {
            searchHistory.addTrack(track)
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(TRACK, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_ITEM_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)

    }
}