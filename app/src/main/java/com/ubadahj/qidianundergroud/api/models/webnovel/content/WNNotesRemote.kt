package com.ubadahj.qidianundergroud.api.models.webnovel.content


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNNotesRemote(
    @Json(name = "avatar")
    val avatar: String,
    @Json(name = "guid")
    val guid: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "penName")
    val penName: String,
    @Json(name = "role")
    val role: String,
    @Json(name = "UUT")
    val uUT: Long
)