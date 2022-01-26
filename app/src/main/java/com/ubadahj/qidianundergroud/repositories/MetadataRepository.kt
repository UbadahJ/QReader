package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import com.ubadahj.qidianundergroud.Database
import com.ubadahj.qidianundergroud.api.WebNovelApi
import com.ubadahj.qidianundergroud.api.models.webnovel.WNBookRemote
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Metadata
import com.ubadahj.qidianundergroud.models.UndergroundBookWithMeta
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val database: Database,
    private val webNovelApi: WebNovelApi
) {

    fun getAll(): Flow<List<Metadata>> =
        database.metadataQueries.selectAll().asFlow().mapToList()

    suspend fun getBook(book: Book, refresh: Boolean = false): Flow<Metadata?> {
        val dbBook = database.bookQueries.getUndergroundById(book.id).executeAsOne()
        val dbMeta = database.metadataQueries.select(dbBook.undergroundId).executeAsOneOrNull()
        if (refresh || dbMeta == null) {
            webNovelApi.getBook(book)?.toMetadata(dbBook)?.also { meta ->
                if (meta != dbMeta) {
                    database.transaction {
                        database.metadataQueries.insert(meta)
                        database.groupQueries.getByBookId(book.id).executeAsList()
                            .filter { "book" in it.link }
                            .forEach { database.groupQueries.deleteByLink(it.link) }
                    }
                }
            }
        }

        return database.metadataQueries.select(dbBook.undergroundId).asFlow().mapToOneNotNull()
    }

    suspend fun setNotifications(book: Book, enable: Boolean) = withContext(Dispatchers.IO) {
        // TODO: Rewrite implementation for notification dismiss
    }

    private fun WNBookRemote.toMetadata(book: UndergroundBookWithMeta): Metadata =
        Metadata(
            id = id,
            bookId = book.undergroundId,
            link = link,
            author = author,
            coverPath = coverLink,
            category = category,
            description = description,
            rating = rating,
            enableNotification = true
        )

}
