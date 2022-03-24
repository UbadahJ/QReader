package com.ubadahj.qidianundergroud.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.utils.coroutines.asSourceFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bookRepo: BookRepository,
    private val groupRepo: GroupRepository
) : ViewModel() {

    private val _selectedBook = MutableStateFlow<Book?>(null).asSourceFlow()
    private val _selectedGroup = MutableStateFlow<Group?>(null).asSourceFlow()

    val selectedBook = _selectedBook.asStateFlow()
    val selectedGroup = _selectedGroup.asStateFlow()

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

    fun getReviews(book: Book) = flow {
        emit(Resource.Loading)
        try {
            emit(
                bookRepo.getReviews(book)
                    .catch { Resource.Error(it) }
                    .map { Resource.Success(it) }
                    .first()
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun clearState() {
        _selectedBook.value = null
        _selectedGroup.value = null
    }

    fun setSelectedBook(book: Book) {
        setSelectedBook(book.id)
    }

    fun setSelectedBook(id: Int) {
        viewModelScope.launch { _selectedBook.emitAll(bookRepo.getBookById(id)) }
    }

    fun setSelectedGroup(group: Group?) {
        setSelectedGroup(group?.link ?: return)
    }

    fun setSelectedGroup(link: String) {
        viewModelScope.launch { _selectedGroup.emitAll(groupRepo.getGroupByLink(link)) }
    }

}
