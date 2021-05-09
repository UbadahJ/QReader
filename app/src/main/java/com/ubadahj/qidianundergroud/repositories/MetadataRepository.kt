package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import com.github.ajalt.timberkt.Timber.d
import com.ubadahj.qidianundergroud.api.WebNovelApi
import com.ubadahj.qidianundergroud.api.models.webnovel.WNBookRemote
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Metadata

class MetadataRepository(private val context: Context) {

    companion object {
        private val cache: MutableMap<String, Metadata?> = mutableMapOf()
    }

    suspend fun getBook(book: Book): Metadata? =
        cache.getOrPut(book.id) {
            WebNovelApi.getBook(book)?.toMetadata(book).also {
                d { "$it" }
            }
        }

    private fun WNBookRemote.toMetadata(book: Book): Metadata =
        Metadata(id, book.id, link, author, coverLink, category, description)

}