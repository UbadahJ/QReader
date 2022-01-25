package com.ubadahj.qidianundergroud.ui.main

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bookRepo: BookRepository,
    private val groupRepo: GroupRepository,
    private val contentRepo: ContentRepository,
    private val metadataRepo: MetadataRepository
) : ViewModel() {

    private var selectedBookJob: Job? = null
    private val _selectedBook: MutableStateFlow<Book?> = MutableStateFlow(null)
    private val _selectedGroup: MutableStateFlow<Group?> = MutableStateFlow(null)
    private val _selectedContent: MutableStateFlow<Content?> = MutableStateFlow(null)

    val selectedBook: StateFlow<Book?> = _selectedBook
    val selectedGroup: StateFlow<Group?> = _selectedGroup
    val selectedContent: StateFlow<Content?> = _selectedContent

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
                bookRepo.getUndergroundBooks(refresh)
                    .catch { Resource.Error(it) }
                    .map { books -> Resource.Success(books) }
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun getWebNovelBook(link: String, refresh: Boolean = false) = flow {
        emit(Resource.Loading)
        try {
            emitAll(
                bookRepo.getWebNovelBook(link, refresh)
                    .catch { Resource.Error(it) }
                    .map { book -> Resource.Success(book) }
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun getChapters(
        book: Book,
        refresh: Boolean = false
    ) = flow {
        emit(Resource.Loading)
        try {
            emitAll(
                groupRepo.getGroups(book, refresh)
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

    fun setSelectedBook(book: Book) {
        setSelectedBook(book.id)
    }

    fun setSelectedBook(id: Int) {
        selectedBookJob?.cancel()
        selectedBookJob = viewModelScope.launch { _selectedBook.emitAll(bookRepo.getBookById(id)) }
    }

    fun setSelectedGroup(group: Group?) {
        _selectedGroup.value = group
        _selectedContent.value = null
    }

    fun setSelectedContent(content: Content?) {
        _selectedContent.value = content
    }

}
