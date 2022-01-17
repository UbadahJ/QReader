package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import android.webkit.WebView
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.ubadahj.qidianundergroud.Database
import com.ubadahj.qidianundergroud.api.UndergroundApi
import com.ubadahj.qidianundergroud.api.WebNovelApi
import com.ubadahj.qidianundergroud.models.Book
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val database: Database,
    private val undergroundApi: UndergroundApi,
    private val webNovelApi: WebNovelApi,
    private val contentRepo: ContentRepository
) {

    fun getBookById(id: Int) = database.bookQueries.getById(id).asFlow().mapToOne()

    suspend fun getUndergroundBooks(refresh: Boolean = false): Flow<List<Book>> {
        val dbBooks = database.bookQueries.getAllUndergroundBooks().executeAsList()
        val dbBookIds = dbBooks.map { it.undergroundId }

        if (refresh || dbBooks.isEmpty()) {
            val books = undergroundApi.getBooks()
            val bookIds = books.map { it.id }

            val (toUpdate, notAvailable) = dbBooks.partition { it.undergroundId in bookIds }
            val toInsert = books.filter { it.id !in dbBookIds }

            database.bookQueries.transaction {
                toUpdate.forEach {
                    database.bookQueries.updateUndergroundBook(
                        it.name, it.lastUpdated, it.completed, it.undergroundId
                    )
                }
                notAvailable.forEach {
                    database.bookQueries.setUndergroundBookAvaliability(
                        false, it.undergroundId
                    )
                }
                toInsert.forEach {
                    database.bookQueries.insertUndergroundBook(
                        it.id,
                        it.name,
                        it.lastUpdated,
                        it.completed
                    )
                }
            }
        }

        return database.bookQueries.getAll().asFlow().mapToList()
    }

    suspend fun getWebNovelBook(link: String, refresh: Boolean = false): Flow<Book> {
        val strippedLink = "/book/${link.trim('/').substringAfterLast("/")}"
        val dbBook = database.bookQueries.getWebNovelByLink(strippedLink).executeAsOneOrNull()
        if (dbBook == null || refresh) {
            val book = webNovelApi.getBook(strippedLink)
                ?: throw IllegalArgumentException("Invalid link: $strippedLink")

            val uBook = database.bookQueries.getByName(book.name).executeAsOneOrNull()
            if (uBook != null) {
                return database.bookQueries.getById(uBook.id).asFlow().mapToOne()
            }

            database.bookQueries.insertWebNovelBook(
                book.id,
                book.name,
                book.link,
                book.author,
                book.coverLink,
                book.category,
                book.description,
                book.rating,
                false
            )
        }

        return database.bookQueries.getById(
            database.bookQueries.getWebNovelByLink(strippedLink).executeAsOne().bookId
        ).asFlow().mapToOne()
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
        val groups = getGroups(book).first()
        groups.forEachIndexed { i, group ->
            var retries = totalRetries
            var success = false
            while (!success || retries < 0) {
                try {
                    contentRepo.getContents(factory, group).first()
                    success = true
                } catch (e: Exception) {
                    if (--retries < 0) throw e
                }
            }
            emit(group)
        }
    }

}
