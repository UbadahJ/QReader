package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import com.ubadahj.qidianundergroud.api.WebNovelApi
import com.ubadahj.qidianundergroud.api.models.webnovel.WNBookRemote
import com.ubadahj.qidianundergroud.database.BookDatabase
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Metadata
import kotlinx.coroutines.flow.Flow

class MetadataRepository(private val context: Context) {

    private val database = BookDatabase.getInstance(context)

    suspend fun getBook(book: Book, refresh: Boolean = false): Flow<Metadata?> {
        val dbMeta = database.metadataQueries.select(book.id).executeAsOneOrNull()
        if (refresh || dbMeta == null) {
            WebNovelApi.getBook(book)?.toMetadata(book)?.also {
                database.metadataQueries.insert(it)
            }
        }

        return database.metadataQueries.select(book.id).asFlow().mapToOneNotNull()
    }

    private fun WNBookRemote.toMetadata(book: Book): Metadata =
        Metadata(id, book.id, link, author, coverLink, category, description)

}