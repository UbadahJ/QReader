package com.ubadahj.qidianundergroud.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bookRepo: BookRepository,
    private val groupRepo: GroupRepository
) : ViewModel() {

    private var selectedBookJob: Job? = null
    private val _selectedBook: MutableStateFlow<Book?> = MutableStateFlow(null)

    val selectedBook: StateFlow<Book?> = _selectedBook

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

    fun clearState() {
        selectedBookJob?.cancel()
        _selectedBook.value = null
    }

    fun setSelectedBook(book: Book) {
        setSelectedBook(book.id)
    }

    fun setSelectedBook(id: Int) {
        selectedBookJob?.cancel()
        selectedBookJob = viewModelScope.launch { _selectedBook.emitAll(bookRepo.getBookById(id)) }
    }

}
