package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.util.Log
import android.view.View
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.inputmethod.EditorInfo
import com.practicum.playlistmaker.apple.Itunes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_SEARCH = "REQUEST_SEARCH"
    }

    private enum class StateType {
        CONNECTION_ERROR, NOT_FOUND, SEARCH_RESULT
    }

    private var editRequest: String = ""


    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(Itunes::class.java)

    private val searchTrackList = ArrayList<Track>()
    private val trackAdapter = TracksAdapter(searchTrackList)
    private lateinit var enteringSearchQuery: EditText
    private lateinit var clearInputQuery: ImageView
    private lateinit var recyclerViewTrack: RecyclerView
    private lateinit var refreshButtPh: Button
    private lateinit var errorTextPh: TextView
    private lateinit var errorIcPh: ImageView
    private lateinit var errorPh: LinearLayout

    private val searchWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

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

        recyclerViewTrack = findViewById(R.id.trackSearch)
        recyclerViewTrack.layoutManager = LinearLayoutManager(this)

        recyclerViewTrack.adapter = trackAdapter

        enteringSearchQuery = findViewById(R.id.enteringSearchQuery)
        enteringSearchQuery.addTextChangedListener(searchWatcher)

        enteringSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrackList()
                true
            }
            false
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
            }
            StateType.NOT_FOUND -> {
                recyclerViewTrack.visibility = View.GONE
                errorPh.visibility = View.VISIBLE
                refreshButtPh.visibility = View.GONE
                errorIcPh.setImageResource(R.drawable.nothing_found)
                errorTextPh.setText(R.string.ph_not_found)
            }
            StateType.SEARCH_RESULT -> {
                recyclerViewTrack.visibility = View.VISIBLE
                errorPh.visibility = View.GONE
                refreshButtPh.visibility = View.GONE
            }
        }
    }

    private fun searchTrackList() {
        if (enteringSearchQuery.text.isNotEmpty()) {
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
}