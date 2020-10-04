package com.ubadahj.qidianundergroud.ui

import android.content.Context
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

    fun getBooks(context: Context, refresh: Boolean = false) =
        BookRepository(context).getBooks(refresh)

    fun getChapters(context: Context, book: Book, refresh: Boolean = false) =
        BookRepository(context).getChapters(book, refresh)

    fun getChapterContents(webView: WebView, book: Book, chapter: ChapterGroup, refresh: Boolean) =
        ChapterRepository(webView.context).getChaptersContent(webView, book, chapter, refresh)

}