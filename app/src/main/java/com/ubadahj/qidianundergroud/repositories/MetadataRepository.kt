package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import com.ubadahj.qidianundergroud.api.WebNovelApi
import com.ubadahj.qidianundergroud.api.models.webnovel.WNBookRemote
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Metadata

class MetadataRepository(private val context: Context) {

    suspend fun getBook(book: Book): Metadata? =
        WebNovelApi.getBook(book)?.toMetadata(book)

    private fun WNBookRemote.toMetadata(book: Book): Metadata =
        Metadata(id, book.id, link, author, coverLink, category, description)

}