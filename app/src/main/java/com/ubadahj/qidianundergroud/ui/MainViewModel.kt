package com.ubadahj.qidianundergroud.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.repositories.BookRepository

class MainViewModel : ViewModel() {

    fun getBooks(refresh: Boolean = false) = BookRepository().getBooks(refresh)

    val selectedBook: MutableLiveData<Book?> by lazy {
        MutableLiveData()
    }

}