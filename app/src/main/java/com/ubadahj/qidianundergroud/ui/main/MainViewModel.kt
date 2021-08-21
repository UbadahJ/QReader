package com.ubadahj.qidianundergroud.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.github.ajalt.timberkt.Timber.e
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Chapter
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository
import com.ubadahj.qidianundergroud.repositories.ChapterRepository
import com.ubadahj.qidianundergroud.repositories.MetadataRepository
import com.ubadahj.qidianundergroud.utils.models.firstChapter
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class MainViewModel : ViewModel() {

    val selectedBook: MutableLiveData<Book?> = MutableLiveData()
    val selectedGroup: MutableLiveData<ChapterGroup?> = MutableLiveData()
    val selectedChapter: MutableLiveData<Chapter?> = MutableLiveData()

    fun libraryBooks(context: Context) = liveData {
        emit(Resource.Loading)
        try {
            emitSource(
                BookRepository(context).getLibraryBooks()
                    .catch { Resource.Error(it) }
                    .map { Resource.Success(it) }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun getBooks(context: Context, refresh: Boolean = false) = liveData {
        emit(Resource.Loading)
        try {
            emitSource(
                BookRepository(context).getBooks(refresh)
                    .catch { Resource.Error(it) }
                    .map { Resource.Success(it) }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun getMetadata(context: Context, book: Book, refresh: Boolean = false) = liveData {
        emit(Resource.Loading)
        try {
            emitSource(
                MetadataRepository(context).getBook(book, refresh)
                    .catch { Resource.Error(it) }
                    .map { Resource.Success(it) }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun getChapters(
        context: Context,
        book: Book,
        refresh: Boolean = false,
        webNovelRefresh: Boolean = false
    ) = liveData {
        emit(Resource.Loading)
        try {
            emitSource(
                ChapterGroupRepository(context).getGroups(book, refresh, webNovelRefresh)
                    .catch { Resource.Error(it) }
                    .map { it.sortedByDescending(ChapterGroup::firstChapter) }
                    .map { Resource.Success(it) }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun getChapterContents(context: Context, group: ChapterGroup, refresh: Boolean) = liveData {
        emit(Resource.Loading)
        try {
            emitSource(
                ChapterRepository(context).getChaptersContent(
                    {
                        WebView(it).apply {
                            settings.javaScriptEnabled = true
                        }
                    },
                    group, refresh
                )
                    .catch { Resource.Error(it) }
                    .map {
                        Resource.Success(it)
                    }
                    .asLiveData()
            )
        } catch (e: Exception) {
            e(e) { "Failed loading content" }
            emit(Resource.Error(e))
        }
    }

}
