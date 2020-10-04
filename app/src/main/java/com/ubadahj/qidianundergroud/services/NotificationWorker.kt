package com.ubadahj.qidianundergroud.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonDataException
import com.ubadahj.qidianundergroud.MainActivity
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.database.DatabaseInstance
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import java.io.IOException
import java.net.SocketException
import kotlin.random.Random

class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    val api = Api(true)
    val database = DatabaseInstance.getInstance(context)

    override suspend fun doWork(): Result {
        val updates: MutableList<Triple<Int, Book, ChapterGroup?>> = mutableListOf()
        for (book in database.get()) {
            try {
                val chapters = api.getChapters(book)
                val lastChapter = chapters.lastChapter()
                val bookLastChapter = book.chapterGroups.lastChapter()
                updates += Triple(
                    lastChapter - bookLastChapter,
                    book,
                    chapters.lastReadChapters(lastChapter)
                )
                if (lastChapter > bookLastChapter) {
                    updates += Triple(
                        lastChapter - bookLastChapter,
                        book,
                        chapters.lastReadChapters(lastChapter + 1)
                    )
                    book.chapterGroups = chapters
                    database.save()
                }
            } catch (e: SocketException) {
            } catch (e: JsonDataException) {
            } catch (e: IOException) {
            }
        }
        createNotificationChannel(applicationContext)
        for (triple in updates) {
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                putExtra("book", triple.second)
                putExtra("chapters", triple.third)
            }
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.book)
                .setContentTitle(triple.second.name)
                .setContentText("${triple.first} new chapter${if (triple.first > 1) "s" else ""} available")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(
                    PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .build()
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(Random(1).nextInt(), notification)
            }
        }
        return Result.success()
    }

    private fun List<ChapterGroup>.lastChapter(): Int {
        return maxByOrNull { it.lastChapter }?.lastChapter ?: 0
    }

    private fun List<ChapterGroup>.lastReadChapters(lastRead: Int) =
        firstOrNull { lastRead in it }

}
