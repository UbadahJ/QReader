package com.ubadahj.qidianundergroud.utils.repositories

import android.content.Context
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.repositories.BookRepository

fun Book.addToLibrary(context: Context) =
    BookRepository(context).addToLibrary(this)

fun Book.updateLastRead(context: Context, lastRead: Int) =
    BookRepository(context).updateLastRead(this, lastRead)

fun Book.getChapters(context: Context) =
    BookRepository(context).getGroups(this)