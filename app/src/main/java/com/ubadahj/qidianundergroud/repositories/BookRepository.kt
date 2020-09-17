package com.ubadahj.qidianundergroud.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Resource

class BookRepository {

    companion object {
        private var books: List<Book>? = null
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

}