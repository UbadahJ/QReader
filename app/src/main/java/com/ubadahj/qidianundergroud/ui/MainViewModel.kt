package com.ubadahj.qidianundergroud.ui

import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository
import com.ubadahj.qidianundergroud.repositories.ChapterRepository
import com.ubadahj.qidianundergroud.ui.adapters.items.ChapterContentItem
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class MainViewModel : ViewModel() {

    val selectedBook: MutableLiveData<Book?> by lazy {
        MutableLiveData()
    }

    val selectedChapter: MutableLiveData<ChapterGroup?> by lazy {
        MutableLiveData()
    }

    fun libraryBooks(context: Context) = BookRepository(context).getLibraryBooks()

    fun getBooks(context: Context, refresh: Boolean = false) = liveData {
        emit(Resource.Loading())
        try {
            emitSource(
                BookRepository(context).getBooks(refresh)
                    .catch { Resource.Error<List<Book>>(it) }
                    .map { Resource.Success(it) }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error<List<Book>>(e))
        }
    }

    fun getChapters(context: Context, book: Book, refresh: Boolean = false) = liveData {
        emit(Resource.Loading())
        try {
            emitSource(
                ChapterGroupRepository(context).getGroups(book, refresh)
                    .catch { Resource.Error<List<ChapterGroup>>(it) }
                    .map { Resource.Success(it) }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error<List<ChapterGroup>>(e))
        }
    }

    fun getChapterContents(webView: WebView, group: ChapterGroup, refresh: Boolean) = liveData {
        emit(Resource.Loading())
        try {
            emitSource(
                ChapterRepository(webView.context).getChaptersContent(webView, group, refresh)
                    .catch { Resource.Error<List<ChapterContentItem>>(it) }
                    .map {
                        Resource.Success(it.map { c ->
                            ChapterContentItem(c.title, c.contents)
                        })
                    }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error<List<ChapterContentItem>>(e))
        }
    }


}