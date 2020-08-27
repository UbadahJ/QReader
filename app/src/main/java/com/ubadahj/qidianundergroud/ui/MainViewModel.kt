package com.ubadahj.qidianundergroud.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubadahj.qidianundergroud.models.Book

class MainViewModel : ViewModel() {

    var bookList: List<Book>? = null
    private val selectedBook: MutableLiveData<Book?> = MutableLiveData()

    fun getSelectedBook(): LiveData<Book?> = selectedBook
    fun updateSelectedBook(book: Book) {
        selectedBook.value = book
    }

}