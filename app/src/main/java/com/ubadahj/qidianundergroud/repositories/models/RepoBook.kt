package com.ubadahj.qidianundergroud.repositories.models

import com.ubadahj.qidianundergroud.Database
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.UndergroundBookWithMeta
import com.ubadahj.qidianundergroud.models.WebNovelBook

fun Book.asRepoBook(database: Database): RepoBook {
    val uBook = database.bookQueries.getUndergroundById(id).executeAsOneOrNull()
    return if (uBook == null) RepoBook.WebNovel(
        database.bookQueries.getWebNovelById(id).executeAsOne()
    ) else RepoBook.Underground(uBook)
}

sealed class RepoBook {
    data class Underground(val book: UndergroundBookWithMeta) : RepoBook()
    data class WebNovel(val book: WebNovelBook) : RepoBook()
}