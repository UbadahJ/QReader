package com.ubadahj.qidianundergroud.ui.main

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.Timber.e
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Content
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.ContentRepository
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.repositories.MetadataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bookRepo: BookRepository,
    private val groupRepo: GroupRepository,
    private val contentRepo: ContentRepository,
    private val metadataRepo: MetadataRepository
) : ViewModel() {

    val selectedBook: MutableStateFlow<Book?> = MutableStateFlow(null)
    val selectedGroup: MutableStateFlow<Group?> = MutableStateFlow(null)
    val selectedChapter: MutableStateFlow<Content?> = MutableStateFlow(null)

    val libraryBooks = flow {
        emit(Resource.Loading)
        try {
            emitAll(
                bookRepo.getLibraryBooks()
                    .catch { Resource.Error(it) }
                    .map { Resource.Success(it) }
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun getBooks(refresh: Boolean = false) = flow {
        emit(Resource.Loading)
        try {
            emitAll(
                bookRepo.getBooks(refresh)
                    .catch { Resource.Error(it) }
                    .map { books -> Resource.Success(books.also { d { "getBooks: ${it.size}" } }) }
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun getMetadata(book: Book, refresh: Boolean = false) = flow {
        emit(Resource.Loading)
        try {
            emitAll(
                metadataRepo.getBook(book, refresh)
                    .catch { Resource.Error(it) }
                    .map { Resource.Success(it) }
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun getChapters(
        book: Book,
        refresh: Boolean = false,
        webNovelRefresh: Boolean = false
    ) = flow {
        emit(Resource.Loading)
        try {
            emitAll(
                groupRepo.getGroups(book, refresh, webNovelRefresh)
                    .catch { Resource.Error(it) }
                    .map { it.sortedByDescending(Group::firstChapter) }
                    .map { Resource.Success(it) }
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun getChapterContents(group: Group, refresh: Boolean) = flow {
        emit(Resource.Loading)
        try {
            emitAll(
                contentRepo.getContents(
                    { WebView(it).apply { settings.javaScriptEnabled = true } },
                    group,
                    refresh
                )
                    .catch { Resource.Error(it) }
                    .map {
                        Resource.Success(it)
                    }
            )
        } catch (e: Exception) {
            e(e) { "Failed loading content" }
            emit(Resource.Error(e))
        }
    }

}
