package com.ubadahj.qidianundergroud.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.d
import com.ubadahj.qidianundergroud.MainActivity
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository
import com.ubadahj.qidianundergroud.utils.models.contains
import com.ubadahj.qidianundergroud.utils.models.lastChapter
import com.ubadahj.qidianundergroud.utils.repositories.getChapters
import kotlinx.coroutines.flow.first

class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private var intentCounter = 0
    private val bookRepo = BookRepository(context)
    private val groupRepo = ChapterGroupRepository(context)

    override suspend fun doWork(): Result {
        val updates: MutableList<Triple<Int, Book, ChapterGroup?>> = mutableListOf()
        for (book in bookRepo.getLibraryBooks().first()) {
            val refreshedGroups = groupRepo.getGroups(book, true).first()
            val lastChapter = refreshedGroups.lastChapter()
            val bookLastChapter = book.getChapters(applicationContext).first().lastChapter()
            if (lastChapter > bookLastChapter) {
                updates += Triple(
                    lastChapter - bookLastChapter,
                    book,
                    refreshedGroups.lastReadChapters(lastChapter + 1)
                )
            }
        }
        d { "doWork: $updates" }
        createNotificationChannel(applicationContext)
        for (triple in updates) {
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.book)
                .setContentTitle(triple.second.name)
                .setContentText("${triple.first} new chapter${if (triple.first > 1) "s" else ""} available")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(createIntent(triple.second, triple.third))
                .addAction(R.drawable.add, "Open latest", createIntent(
                    triple.second,
                    triple.second.getChapters(applicationContext).first()
                        .maxByOrNull { it.lastChapter }
                ))
                .addAction(R.drawable.add, "Open Book", createIntent(triple.second))
                .setAutoCancel(true)
                .build()
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(triple.hashCode(), notification)
            }
        }
        return Result.success()
    }

    private fun createIntent(
        book: Book?,
        chapters: ChapterGroup? = null,
        requestCode: Int = intentCounter++
    ) =
        PendingIntent.getActivity(
            applicationContext,
            requestCode,
            Intent(applicationContext, MainActivity::class.java).apply {
                putExtra("book", book?.id)
                putExtra("chapters", chapters?.link)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun List<ChapterGroup>.lastChapter(): Int {
        return maxByOrNull { it.lastChapter }?.lastChapter ?: 0
    }

    private fun List<ChapterGroup>.lastReadChapters(lastRead: Int) =
        firstOrNull { lastRead in it }

}
