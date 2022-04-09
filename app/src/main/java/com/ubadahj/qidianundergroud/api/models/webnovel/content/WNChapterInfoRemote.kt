package com.ubadahj.qidianundergroud.api.models.webnovel.content


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNChapterInfoRemote(
    @Json(name = "adPosition")
    val adPosition: WNAdPositionRemote,
    @Json(name = "announcementItems")
    val announcementItems: List<Any>,
    @Json(name = "banner")
    val banner: WNBannerRemote,
    @Json(name = "batchUnlockStatus")
    val batchUnlockStatus: Int,
    @Json(name = "chapterId")
    val chapterId: String,
    @Json(name = "chapterIndex")
    val chapterIndex: Int,
    @Json(name = "chapterLevel")
    val chapterLevel: Int,
    @Json(name = "chapterName")
    val chapterName: String,
    @Json(name = "chapterReviewItems")
    val chapterReviewItems: List<WNChapterReviewItemRemote>,
    @Json(name = "contents")
    val contents: List<WNContentRemote>,
    @Json(name = "discountInfo")
    val discountInfo: String,
    @Json(name = "editorItems")
    val editorItems: List<Any>,
    @Json(name = "encryptKeyPool")
    val encryptKeyPool: String,
    @Json(name = "encryptType")
    val encryptType: Int,
    @Json(name = "encryptVersion")
    val encryptVersion: Int,
    @Json(name = "firstChapterId")
    val firstChapterId: String,
    @Json(name = "firstChapterIndex")
    val firstChapterIndex: Int,
    @Json(name = "groupItems")
    val groupItems: List<Any>,
    @Json(name = "isAuth")
    val isAuth: Int,
    @Json(name = "isRichFormat")
    val isRichFormat: Int,
    @Json(name = "nextChapterId")
    val nextChapterId: String,
    @Json(name = "nextChapterName")
    val nextChapterName: String,
    @Json(name = "noArchive")
    val noArchive: Int,
    @Json(name = "notes")
    val notes: WNNotesRemote,
    @Json(name = "orderIndex")
    val orderIndex: Int,
    @Json(name = "originalPrice")
    val originalPrice: Int,
    @Json(name = "preChapterId")
    val preChapterId: String,
    @Json(name = "preChapterName")
    val preChapterName: String,
    @Json(name = "price")
    val price: Int,
    @Json(name = "reviewTotal")
    val reviewTotal: Int,
    @Json(name = "transRating")
    val transRating: Int,
    @Json(name = "translatorItems")
    val translatorItems: List<WNTranslatorItemRemote>,
    @Json(name = "userLevel")
    val userLevel: Int,
    @Json(name = "vipStatus")
    val vipStatus: Int
)