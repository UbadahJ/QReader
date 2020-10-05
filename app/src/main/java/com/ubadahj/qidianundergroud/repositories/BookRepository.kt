package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.api.models.BookJson
import com.ubadahj.qidianundergroud.database.BookDatabase
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Resource

class BookRepository(context: Context) {

    private val database = BookDatabase.getInstance(context)

    fun getBooks(): List<Book> = database.bookQueries.getAll().executeAsList()

    fun getBookById(id: String) = database.bookQueries.getById(id).executeAsOneOrNull()

    fun getBooks(refresh: Boolean = false): LiveData<Resource<List<Book>>> = liveData {
        emit(Resource.Loading())
        try {
            val dbBookIds = database.bookQueries.getAll().executeAsList().map { it.id }
            if (refresh || dbBookIds.isEmpty()) {
                val books = Api(proxy = true).getBooks().map { it.toBook() }
                for (book in books.filter { it.id !in dbBookIds })
                    database.bookQueries.insert(book)

                for (book in books.filter { it.id in dbBookIds })
                    database.bookQueries.update(
                        book.name,
                        book.lastUpdated,
                        book.completed,
                        book.id
                    )
            }

            emit(Resource.Success(database.bookQueries.getAll().executeAsList()))
        } catch (e: Exception) {
            emit(Resource.Error<List<Book>>(e))
        }
    }

    fun getLibraryBooks() = database.bookQueries.getAllLibraryBooks().executeAsList()

    fun getChapters(book: Book) = database.chapterGroupQueries.getByBookId(book.id).executeAsList()

    fun addToLibrary(book: Book) {
        if (database.bookQueries.getById(book.id).executeAsOneOrNull() == null)
            throw IllegalArgumentException("$this does not exists in library")

        database.bookQueries.addToLibrary(book.id)
    }

    fun updateLastRead(book: Book, lastRead: Int) {
        if (database.bookQueries.getById(book.id).executeAsOneOrNull() == null)
            throw IllegalArgumentException("$this does not exists in library")

        database.bookQueries.updateLastRead(lastRead, book.id)
    }

    private fun BookJson.toBook() = Book(id, name, lastUpdated, completed, inLibrary, lastRead)

}