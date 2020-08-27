package com.ubadahj.qidianundergroud.database

import androidx.room.TypeConverter
import com.squareup.moshi.Types
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.moshi

class ChapterGroupConverter {

    val adapter = moshi.adapter<List<ChapterGroup>>(
        Types.newParameterizedType(List::class.java, ChapterGroup::class.java)
    )

    @TypeConverter
    fun toChapterGroup(json: String): List<ChapterGroup> {
        return adapter.fromJson(json)!!
    }

    @TypeConverter
    fun toJsonString(groups: List<ChapterGroup>): String {
        return adapter.toJson(groups)
    }

}