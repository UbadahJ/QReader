package com.ubadahj.qidianundergroud.api.models.webnovel

data class WNBookRemote(
    val id: String,
    val link: String,
    val name: String,
    val author: String,
    val coverLink: String,
    val category: String,
    val description: String,
)