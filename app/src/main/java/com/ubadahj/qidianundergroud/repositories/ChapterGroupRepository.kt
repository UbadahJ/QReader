package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import androidx.lifecycle.liveData
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.api.models.ChapterGroupJson
import com.ubadahj.qidianundergroud.database.BookDatabase
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource

class ChapterGroupRepository(context: Context) {

    private val database = BookDatabase.getInstance(context)

    fun getBook(group: ChapterGroup) = database.chapterGroupQueries.getByBookId(group.bookId)

    fun getGroupByLink(link: String) = database.chapterGroupQueries.get(link).executeAsOneOrNull()

    fun getGroups(book: Book, refresh: Boolean = false) = liveData {
        emit(Resource.Loading())
        try {
            val dbGroups = database.bookQueries.chapters(book.id).executeAsList()
            if (refresh || dbGroups.isEmpty()) {
                val groups = Api(proxy = true).getChapters(book.id)
                    .map { it.toGroup(book) }

                database.chapterGroupQueries.transaction {
                    for (group in groups)
                        database.chapterGroupQueries.insert(group)

                    for (group in dbGroups.filter { it !in groups })
                        database.chapterGroupQueries.deleteByLink(group.link)
                }
            }
            emit(Resource.Success(database.bookQueries.chapters(book.id).executeAsList()))
        } catch (e: Exception) {
            emit(Resource.Error<List<ChapterGroup>>(e))
        }
    }

    private fun ChapterGroupJson.toGroup(book: Book) = ChapterGroup(book.id, text, link)

}