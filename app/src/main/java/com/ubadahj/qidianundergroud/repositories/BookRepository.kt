package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.database.DatabaseInstance
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource

class BookRepository(context: Context) {

    companion object {
        private var books: List<Book>? = null
        private var chapters: MutableMap<String, List<ChapterGroup>> = mutableMapOf()
    }

    private val database = DatabaseInstance.getInstance(context)

    fun getBooks(refresh: Boolean = false): LiveData<Resource<List<Book>>> = liveData {
        emit(Resource.Loading())
        try {
            if (refresh || books == null)
                books = Api(proxy = true).getBooks()

            database.add(*(books!!.filter { it !in database.get() }).toTypedArray())
            emit(Resource.Success(books!!))
        } catch (e: Exception) {
            emit(Resource.Error<List<Book>>(e))
        }
    }

    fun getChapters(book: Book, refresh: Boolean = false) = liveData {
        emit(Resource.Loading())
        try {
            if (refresh || book.chapterGroups.isEmpty())
                book.chapterGroups = Api(proxy = true).getChapters(book)

            database.save()
            emit(Resource.Success(book.chapterGroups))
        } catch (e: Exception) {
            emit(Resource.Error<List<ChapterGroup>>(e))
        }
    }

}