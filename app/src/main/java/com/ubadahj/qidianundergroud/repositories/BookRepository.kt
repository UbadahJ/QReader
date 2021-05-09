package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import android.webkit.WebView
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.api.models.undeground.BookJson
import com.ubadahj.qidianundergroud.database.BookDatabase
import com.ubadahj.qidianundergroud.models.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class BookRepository(val context: Context) {

    private val database = BookDatabase.getInstance(context)

    fun getBookById(id: String) = database.bookQueries.getById(id).asFlow().mapToOne()

    suspend fun getBooks(refresh: Boolean = false): Flow<List<Book>> {
        val dbBookIds = database.bookQueries.getAll().executeAsList().map { it.id }
        if (refresh || dbBookIds.isEmpty()) {
            val books = Api(proxy = true).getBooks().map { it.toBook() }
            database.bookQueries.transaction {
                books.forEach { book ->
                    database.bookQueries.upsert(
                        book.name,
                        book.lastUpdated,
                        book.completed,
                        book.id
                    )
                }
            }
        }

        return database.bookQueries.getAll().asFlow().mapToList()
    }

    fun getLibraryBooks() = database.bookQueries.getAllLibraryBooks().asFlow().mapToList()

    fun getGroups(book: Book) =
        database.bookQueries.chapters(book.id).asFlow().mapToList()

    suspend fun addToLibrary(book: Book) = withContext(Dispatchers.IO) {
        if (database.bookQueries.getById(book.id).executeAsOneOrNull() == null)
            throw IllegalArgumentException("$book does not exists in library")

        database.bookQueries.addToLibrary(book.id)
    }

    suspend fun removeFromLibrary(book: Book) = withContext(Dispatchers.IO) {
        if (!database.bookQueries.getById(book.id).executeAsOne().inLibrary)
            throw IllegalArgumentException("$book already exists in library")

        database.bookQueries.removeFromLibrary(book.id)
    }

    suspend fun markAllRead(book: Book) = withContext(Dispatchers.IO) {
        database.bookQueries.markAllRead(book.id)
    }

    fun download(book: Book, factory: (Context) -> WebView, totalRetries: Int = 3) = flow {
        val chapterRepo = ChapterRepository(context)
        val groups = getGroups(book).first()
        groups.forEachIndexed { i, group ->
            var retries = totalRetries
            var success = false
            while (!success || retries < 0) {
                try {
                    chapterRepo.getChaptersContent(factory, group).first()
                    success = true
                } catch (e: Exception) {
                    if (--retries < 0) throw e
                }
            }
            emit(group)
        }
    }

    private fun BookJson.toBook() = Book(id, name, lastUpdated, completed, inLibrary)

}