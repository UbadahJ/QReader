package com.ubadahj.qidianundergroud.database

import android.content.SharedPreferences
import com.squareup.moshi.Types
import com.ubadahj.qidianundergroud.models.Book

class Database(private val preferences: SharedPreferences) {

    private val adapter = moshi.adapter<MutableList<Book>>(
        Types.newParameterizedType(MutableList::class.java, Book::class.java)
    )
    private val books: MutableList<Book> = adapter.fromJson(
        preferences.getString("books", "[]")!!
    )!!

    fun get(): List<Book> = books

    fun add(vararg books: Book) {
        this.books.addAll(books)
        save()
    }

    fun delete(book: Book) {
        books.remove(book)
        save()
    }

    private fun save() {
        preferences.edit().putString("books", adapter.toJson(books)).apply()
    }

}