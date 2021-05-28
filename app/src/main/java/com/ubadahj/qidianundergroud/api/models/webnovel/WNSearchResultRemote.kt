package com.ubadahj.qidianundergroud.api.models.webnovel

data class WNSearchResultRemote(
    val name: String,
    val link: String,
    val tags: List<String>,
    val rating: Float,
    val desc: String,
) {
    val id: String
        get() = link.substringAfterLast("_")
}
