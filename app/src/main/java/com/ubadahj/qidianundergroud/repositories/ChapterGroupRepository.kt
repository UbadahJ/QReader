package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.api.WebNovelApi
import com.ubadahj.qidianundergroud.api.models.undeground.ChapterGroupJson
import com.ubadahj.qidianundergroud.api.models.webnovel.WNChapterRemote
import com.ubadahj.qidianundergroud.database.BookDatabase
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.utils.models.firstChapter
import com.ubadahj.qidianundergroud.utils.models.lastChapter
import com.ubadahj.qidianundergroud.utils.models.total
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ChapterGroupRepository(context: Context) {

    private val database = BookDatabase.getInstance(context)
    private val metaRepo = MetadataRepository(context)

    fun getBook(group: ChapterGroup) = database.bookQueries
        .getById(group.bookId)
        .asFlow()
        .mapToOne()

    fun getChaptersByBook(group: ChapterGroup) = database.chapterGroupQueries
        .getByBookId(group.bookId)
        .asFlow()
        .mapToList()

    fun getGroupByLink(link: String) = database.chapterGroupQueries.get(link).asFlow().mapToOne()

    fun isDownloaded(group: ChapterGroup): Boolean =
        database.chapterQueries
            .getByGroupLink(group.link)
            .executeAsList()
            .size == group.total

    fun updateLastRead(group: ChapterGroup, lastRead: Int) {
        if (database.chapterGroupQueries.get(group.link).executeAsOneOrNull() == null)
            throw IllegalArgumentException("$this chapter group does not exists")

        database.chapterGroupQueries.updateLastRead(lastRead, group.link)
    }

    suspend fun getGroups(
        book: Book,
        refresh: Boolean = false,
        webNovelRefresh: Boolean = false
    ): Flow<List<ChapterGroup>> {
        val dbGroups = database.bookQueries.chapters(book.id).executeAsList()
        if (refresh || dbGroups.isEmpty()) {
            val remoteGroups = Api(proxy = true)
                .getChapters(book.id)
                .map { it.toGroup(book) }

            val remoteChapters = remoteGroups.associateBy { it.firstChapter }
            val dbGroupsToUpdate = dbGroups
                .filter { it.firstChapter in remoteChapters.keys }
                .filter { it.lastChapter != remoteChapters[it.firstChapter]?.lastChapter }

            database.chapterGroupQueries.transaction {
                for (group in dbGroupsToUpdate) {
                    val remoteGroup = remoteChapters[group.firstChapter]!!
                    // We need to delete all the previous chapters to make sure foreign key
                    // doesn't fail
                    database.chapterQueries.deleteByGroupLink(group.link)
                    database.chapterGroupQueries.update(
                        link = group.link,
                        updatedText = remoteGroup.text,
                        updatedLink = remoteGroup.link
                    )
                }

                // Due to INSERT OR IGNORE, we can ignore same entries
                for (group in remoteGroups)
                    database.chapterGroupQueries.insert(group)
            }
        }

        if (webNovelRefresh || dbGroups.isEmpty()) {
            val remoteWebNovelChapters = metaRepo.getBook(book, refresh).first()
                ?.let { WebNovelApi.getChapter(it) }
                ?.filter { !it.premium }
                ?.map { it.toGroup(book) }
                ?: listOf()

            database.chapterGroupQueries.transaction {
                for (chapter in remoteWebNovelChapters)
                    database.chapterGroupQueries.insert(chapter)
            }
        }

        return database.bookQueries.chapters(book.id).asFlow().mapToList()
    }

    private fun ChapterGroupJson.toGroup(book: Book) = ChapterGroup(book.id, text, link, 0)

    private fun WNChapterRemote.toGroup(book: Book) =
        ChapterGroup(book.id, index.toString(), link, 0)

}
