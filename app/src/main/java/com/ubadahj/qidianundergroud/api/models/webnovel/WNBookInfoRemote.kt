package com.ubadahj.qidianundergroud.api.models.webnovel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNBookInfoRemote(
    @Json(name = "bookId")
    val bookId: String,
    @Json(name = "bookName")
    val bookName: String,
    @Json(name = "hasPrivilege")
    val hasPrivilege: Int,
    @Json(name = "totalChapterNum")
    val totalChapterNum: Int
)
