package com.ubadahj.qidianundergroud.database

import android.content.Context

class DatabaseInstance private constructor(context: Context) {

    companion object {
        private var instance: DatabaseInstance? = null

        fun getInstance(context: Context): Database {
            if (instance == null)
                instance = DatabaseInstance(context)
            return instance!!.db
        }
    }

    val db: Database = Database(
        context.getSharedPreferences("db", Context.MODE_PRIVATE)
    )
}