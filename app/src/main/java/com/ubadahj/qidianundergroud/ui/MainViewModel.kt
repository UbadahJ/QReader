package com.ubadahj.qidianundergroud.ui

import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository
import com.ubadahj.qidianundergroud.repositories.ChapterRepository

class MainViewModel : ViewModel() {

    val selectedBook: MutableLiveData<Book?> by lazy {
        MutableLiveData()
    }

    val selectedChapter: MutableLiveData<ChapterGroup?> by lazy {
        MutableLiveData()
    }

    fun libraryBooks(context: Context) = BookRepository(context).getLibraryBooks()

    fun getBooks(context: Context, refresh: Boolean = false) =
        BookRepository(context).getBooks(refresh).asLiveData()

    fun getChapters(context: Context, book: Book, refresh: Boolean = false) =
        ChapterGroupRepository(context).getGroups(book, refresh).asLiveData()

    fun getChapterContents(webView: WebView, group: ChapterGroup, refresh: Boolean) =
        ChapterRepository(webView.context).getChaptersContent(webView, group, refresh).asLiveData()

}