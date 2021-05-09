package com.ubadahj.qidianundergroud.utils.repositories

import android.content.Context
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.repositories.BookRepository

suspend fun Book.addToLibrary(context: Context) =
    BookRepository(context).addToLibrary(this)

suspend fun Book.removeFromLibrary(context: Context) =
    BookRepository(context).removeFromLibrary(this)

fun Book.getChapters(context: Context) =
    BookRepository(context).getGroups(this)