package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private val allTracks = listOf(
        Track("Smells Like Teen Spirit", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
        Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
        Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
        Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
        Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
    )

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        var searchQuery: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация компонентов
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        inputEditText = findViewById(R.id.findEditText)
        clearIcon = findViewById(R.id.clearTextIcon)
        trackRecyclerView = findViewById(R.id.trackRecyclerView)

        // Инициализация адаптера для RecyclerView
        trackAdapter = TrackAdapter(emptyList())
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter

        // Обработчик кнопки назад
        toolbar.setOnClickListener { finish() }

        // Восстановление текста из сохраненного состояния
        inputEditText.setText(searchQuery)

        // Обработка иконки очистки текста
        clearIcon.setOnClickListener {
            clearInputText()
        }

        // Слушатель изменения текста в поле ввода
        inputEditText.addTextChangedListener { text ->
            searchQuery = text.toString()

            if (searchQuery.isNotEmpty()) {
                val filteredTracks = allTracks.filter { track ->
                    track.trackName.contains(searchQuery, ignoreCase = true) ||
                            track.artistName.contains(searchQuery, ignoreCase = true)
                }
                trackAdapter = TrackAdapter(filteredTracks)
                trackRecyclerView.adapter = trackAdapter
                trackRecyclerView.visibility = View.VISIBLE
            } else {
                trackRecyclerView.visibility = View.GONE
            }

            // Показ/скрытие иконки очистки
            clearIcon.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        // "Done"
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleSearchAction()
                true
            } else {
                false
            }
        }

        // Настройка отступов для системы навигации
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Очистка текста в поле и скрытие клавиатуры
    private fun clearInputText() {
        inputEditText.setText("")
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    // Обработка кнопки поиска
    private fun handleSearchAction() {
        val inputText = inputEditText.text.toString()
        if (inputText.isNotEmpty()) {
            Toast.makeText(this, "Поиск: $inputText", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Введите запрос для поиска", Toast.LENGTH_SHORT).show()
        }
    }

    // Сохранение состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, inputEditText.text.toString())
    }

    // Восстановление состояния
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputEditText.setText(savedInstanceState.getString(SEARCH_TEXT_KEY))
    }

}
