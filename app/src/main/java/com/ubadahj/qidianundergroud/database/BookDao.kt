package com.ubadahj.qidianundergroud.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ubadahj.qidianundergroud.models.Book

@Dao
interface BookDao {

    @Query("SELECT * FROM book WHERE ID = :id")
    fun select(id: String): Book

    @Query("SELECT * FROM book")
    fun selectAll(): List<Book>

    @Insert
    fun insert(vararg books: Book)

    @Delete
    fun delete(book: Book)

}