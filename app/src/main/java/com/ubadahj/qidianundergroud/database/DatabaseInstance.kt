package com.ubadahj.qidianundergroud.database

import android.content.Context
import androidx.room.Room

class DatabaseInstance private constructor(context: Context) {

    companion object {
        private var instance: DatabaseInstance? = null

        fun getInstance(context: Context): Database {
            if (instance == null)
                instance = DatabaseInstance(context)
            return instance!!.db
        }
    }

    val db: Database = Room.databaseBuilder(
        context, Database::class.java, "appDb"
    ).build()

}