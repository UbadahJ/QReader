package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import android.webkit.WebView
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.ubadahj.qidianundergroud.Database
import com.ubadahj.qidianundergroud.api.UndergroundApi
import com.ubadahj.qidianundergroud.api.WebNovelApi
import com.ubadahj.qidianundergroud.models.Content
import com.ubadahj.qidianundergroud.models.Group
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val database: Database,
    private val undergroundApi: UndergroundApi,
    private val webNovelApi: WebNovelApi
) {

    suspend fun getContents(
        refresh: Boolean = false,
        group: Group,
        webViewFactory: (Context) -> WebView
    ): Flow<List<Content>> {
        val dbChapters = database.groupQueries.contents(group.link).executeAsList()
        if (refresh || dbChapters.isEmpty()) {
            when {
                "vim" in group.link -> fetchDefaultChapters(webViewFactory, group)
                "pstbn" in group.link -> fetchDefaultChapters(webViewFactory, group)
                else -> fetchWebNovelChapters(group)
            }
        }

        return database.groupQueries.contents(group.link).asFlow().mapToList()
    }

    private suspend fun fetchWebNovelChapters(group: Group) {
        database.contentQueries.insert(webNovelApi.getChapterContents(group))
    }

    private suspend fun fetchDefaultChapters(
        webViewFactory: (Context) -> WebView,
        group: Group,
    ) {
        val contents = undergroundApi.getContents(webViewFactory, group)
        database.transaction {
            contents.forEach { database.contentQueries.insert(it) }
        }
    }

}
