package com.ubadahj.qidianundergroud.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.ubadahj.qidianundergroud.Database

class BookDatabase private constructor(context: Context) {

    companion object {
        private var database: BookDatabase? = null

        fun getInstance(context: Context): Database {
            if (database == null)
                database = BookDatabase(context)
            return database!!.database
        }
    }

    private val driver: SqlDriver = AndroidSqliteDriver(
        Database.Schema,
        context,
        "book.db",
        callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
            override fun onConfigure(db: SupportSQLiteDatabase) {
                super.onConfigure(db)
                db.setForeignKeyConstraintsEnabled(true)
            }
        }
    )
    val database = Database(driver)
}