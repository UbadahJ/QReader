package com.ubadahj.qidianundergroud.ui

import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.ChapterRepository

class MainViewModel : ViewModel() {

    val selectedBook: MutableLiveData<Book?> by lazy {
        MutableLiveData()
    }

    val selectedChapter: MutableLiveData<ChapterGroup?> by lazy {
        MutableLiveData()
    }

    fun getBooks(refresh: Boolean = false) = BookRepository().getBooks(refresh)

    fun getChapters(book: Book, refresh: Boolean = false) =
        BookRepository().getChapters(book, refresh)

    fun getChapterContents(webView: WebView, book: Book, chapter: ChapterGroup, refresh: Boolean) =
        ChapterRepository().getChaptersContent(webView, book, chapter, refresh)

}