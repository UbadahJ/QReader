package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.api.models.ChapterGroupJson
import com.ubadahj.qidianundergroud.database.BookDatabase
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import kotlinx.coroutines.flow.Flow

class ChapterGroupRepository(context: Context) {

    private val database = BookDatabase.getInstance(context)

    fun getBook(group: ChapterGroup) = database.bookQueries
        .getById(group.bookId)
        .asFlow()
        .mapToOne()

    fun getChaptersByBook(group: ChapterGroup) = database.chapterGroupQueries
        .getByBookId(group.bookId)
        .asFlow()
        .mapToList()

    fun getGroupByLink(link: String) = database.chapterGroupQueries.get(link).asFlow().mapToOne()

    suspend fun getGroups(book: Book, refresh: Boolean = false): Flow<List<ChapterGroup>> {
        val dbGroups = database.bookQueries.chapters(book.id).executeAsList()
        if (refresh || dbGroups.isEmpty()) {
            val groups = Api(proxy = true).getChapters(book.id)
                .map { it.toGroup(book) }
            database.chapterGroupQueries.transaction {
                for (group in dbGroups.filter { it !in groups })
                    database.chapterGroupQueries.deleteByLink(group.link)

                for (group in groups)
                    database.chapterGroupQueries.insert(group)
            }
        }

        return database.bookQueries.chapters(book.id).asFlow().mapToList()
    }

    private fun ChapterGroupJson.toGroup(book: Book) = ChapterGroup(book.id, text, link)

}