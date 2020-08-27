package com.ubadahj.qidianundergroud.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ubadahj.qidianundergroud.models.Book

@Database(entities = [Book::class], version = 1)
@TypeConverters(ChapterGroupConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun bookDao(): BookDao

}