package com.ubadahj.qidianundergroud.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.ubadahj.qidianundergroud.models.*
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
        emit(Resource.Loading())
        try {
            emitSource(
                BookRepository(context).getLibraryBooks()
                    .catch { Resource.Error<List<Book>>(it) }
                    .map { Resource.Success(it) }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error<List<Book>>(e))
        }
    }

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

    fun getMetadata(context: Context, book: Book, refresh: Boolean = false) = liveData {
        emit(Resource.Loading())
        try {
            emitSource(
                MetadataRepository(context).getBook(book, refresh)
                    .catch { Resource.Error<Metadata?>(it) }
                    .map { Resource.Success(it) }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error<Metadata?>(e))
        }
    }

    fun getChapters(context: Context, book: Book, refresh: Boolean = false) = liveData {
        emit(Resource.Loading())
        try {
            emitSource(
                ChapterGroupRepository(context).getGroups(book, refresh)
                    .catch { Resource.Error<List<ChapterGroup>>(it) }
                    .map { it.sortedByDescending(ChapterGroup::firstChapter) }
                    .map { Resource.Success(it) }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error<List<ChapterGroup>>(e))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun getChapterContents(context: Context, group: ChapterGroup, refresh: Boolean) = liveData {
        emit(Resource.Loading())
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
                    .catch { Resource.Error<List<Chapter>>(it) }
                    .map {
                        Resource.Success(it)
                    }
                    .asLiveData()
            )
        } catch (e: Exception) {
            emit(Resource.Error<List<Chapter>>(e))
        }
    }

}
