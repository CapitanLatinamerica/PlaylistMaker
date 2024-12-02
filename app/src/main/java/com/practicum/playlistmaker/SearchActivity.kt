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
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView

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
            clearIcon.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        // Обработка действия "Done" на клавиатуре
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
