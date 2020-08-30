package com.ubadahj.qidianundergroud.services

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonDataException
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
        val updates: MutableList<Pair<Int, Book>> = mutableListOf()
        for (book in database.get()) {
            try {
                val chapters = api.getChapters(book)
                val lastChapter = chapters.lastChapter()
                val bookLastChapter = book.chapterGroups.lastChapter()
                if (lastChapter > bookLastChapter) {
                    updates.add(Pair(bookLastChapter - lastChapter, book))
                    book.chapterGroups = chapters
                    database.save()
                }
            } catch (e: SocketException) {
            } catch (e: JsonDataException) {
            } catch (e: IOException) {
            }
        }
        createNotificationChannel(applicationContext)
        for (pair in updates) {
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.book)
                .setContentTitle("${pair.second.name} updated")
                .setContentText("${pair.first} new chapter${if (pair.first > 1) "s" else ""}")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(Random(1).nextInt(), notification)
            }
        }
        return Result.success()
    }

    fun List<ChapterGroup>.lastChapter(): Int {
        return maxByOrNull { it.lastChapter }?.lastChapter ?: 0
    }

}
