package com.ubadahj.qidianundergroud.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubadahj.qidianundergroud.models.Book

class MainViewModel : ViewModel() {

    var bookList: List<Book>? = null
    val selectedBook: MutableLiveData<Book?> by lazy {
        MutableLiveData()
    }

}