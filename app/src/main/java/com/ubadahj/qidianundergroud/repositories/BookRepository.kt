package com.ubadahj.qidianundergroud.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource

class BookRepository {

    companion object {
        private var books: List<Book>? = null
        private var chapters: MutableMap<String, List<ChapterGroup>> = mutableMapOf()
    }

    fun getBooks(refresh: Boolean = false): LiveData<Resource<List<Book>>> = liveData {
        emit(Resource.Loading())
        try {
            if (refresh || books == null)
                books = Api(proxy = true).getBooks()

            emit(Resource.Success(books!!))
        } catch (e: Exception) {
            emit(Resource.Error<List<Book>>(e))
        }
    }

    fun getChapters(book: Book, refresh: Boolean = false) = liveData {
        emit(Resource.Loading())
        try {
            if (refresh || book.id !in chapters)
                chapters[book.id] = Api(proxy = true).getChapters(book)

            emit(Resource.Success(chapters[book.id]!!))
        } catch (e: Exception) {
            emit(Resource.Error<List<ChapterGroup>>(e))
        }
    }

}