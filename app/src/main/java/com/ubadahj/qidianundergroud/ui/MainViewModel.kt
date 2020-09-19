package com.ubadahj.qidianundergroud.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.repositories.BookRepository

class MainViewModel : ViewModel() {

    val selectedBook: MutableLiveData<Book?> by lazy {
        MutableLiveData()
    }

    fun getBooks(refresh: Boolean = false) = BookRepository().getBooks(refresh)

    fun getChapters(book: Book, refresh: Boolean = false) =
        BookRepository().getChapters(book, refresh)

}