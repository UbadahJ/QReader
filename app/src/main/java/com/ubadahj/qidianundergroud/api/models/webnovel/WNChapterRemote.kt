package com.ubadahj.qidianundergroud.api.models.webnovel

data class WNChapterRemote(
    val title: String,
    val link: String,
    val index: Int? = null,
    val premium: Boolean = false
)
