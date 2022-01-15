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
        val dbMeta = database.metadataQueries.select(book.id).executeAsOneOrNull()
        if (refresh || dbMeta == null) {
            webNovelApi.getBook(book)?.toMetadata(book)?.also { meta ->
                if (meta != dbMeta) {
                    database.transaction {
                        database.metadataQueries.insert(meta)
                        database.chapterQueries.getByBookId(meta.bookId).executeAsList()
                            .filter { "book" in it.link }
                            .forEach { database.chapterQueries.deleteByLink(it.link) }
                    }
                }
            }
        }

        return database.metadataQueries.select(book.id).asFlow().mapToOneNotNull()
    }

    suspend fun setNotifications(book: Book, enable: Boolean) = withContext(Dispatchers.IO) {
        database.metadataQueries.updateNotify(enable, book.id)
    }

    private fun WNBookRemote.toMetadata(book: Book): Metadata =
        Metadata(id, book.id, link, author, coverLink, category, description, rating, true)

}
