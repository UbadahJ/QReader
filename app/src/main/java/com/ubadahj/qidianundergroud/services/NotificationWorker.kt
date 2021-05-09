package com.ubadahj.qidianundergroud.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.e
import com.ubadahj.qidianundergroud.MainActivity
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository
import com.ubadahj.qidianundergroud.repositories.MetadataRepository
import com.ubadahj.qidianundergroud.utils.models.lastChapter
import com.ubadahj.qidianundergroud.utils.repositories.getChapters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val bookRepo = BookRepository(context)
    private val groupRepo = ChapterGroupRepository(context)
    private val metaRepo = MetadataRepository(context)
    private val notificationId = 42069

    override suspend fun doWork(): Result {
        createNotificationChannel(applicationContext)
        getNotifications().collect {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(it.hashCode(), it.createNotification())
            }
        }
        return Result.success()
    }

    private fun getNotifications() = flow {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
            setContentTitle("Checking for updates")
            setSmallIcon(R.drawable.app_image_outline)
            setOnlyAlertOnce(true)
            priority = NotificationCompat.PRIORITY_LOW
        }
        val books = bookRepo.getLibraryBooks().first()
            .filter { metaRepo.getBook(it).first()?.enableNotification == true }

        NotificationManagerCompat.from(applicationContext).apply {
            builder.setProgress(books.size, 0, false)
            notify(notificationId, builder.build())

            var counter = 0
            for (book in books) {
                try {
                    val lastGroup = book.getChapters(applicationContext).first()
                    val refreshedGroups = groupRepo.getGroups(book, true).first()
                    val updateCount = refreshedGroups.lastChapter() - lastGroup.lastChapter()
                    builder.setContentText(book.name)
                    builder.setProgress(books.size, ++counter, false)
                    notify(notificationId, builder.build())
                    if (updateCount > 0)
                        emit(
                            BookNotification(applicationContext, book, refreshedGroups, updateCount)
                        )
                } catch (e: Exception) {
                    e(e) { "getNotifications: Failed to query $book" }
                }
            }

            builder.setProgress(0, 0, false)
            cancel(notificationId)
        }
    }.flowOn(Dispatchers.IO)

    private fun List<ChapterGroup>.lastChapter(): Int {
        return maxByOrNull { it.lastChapter }?.lastChapter ?: 0
    }

    private data class BookNotification(
        val context: Context,
        val book: Book,
        val groups: List<ChapterGroup>,
        val updateCount: Int
    ) {

        val lastGroup = groups.maxByOrNull { it.lastChapter }!!

        fun createNotification(): Notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_image_outline)
                .setContentTitle(book.name)
                .setContentText("$updateCount new chapter${if (updateCount > 1) "s" else ""} available")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(createIntent(lastGroup))
                .addAction(R.drawable.add, "Open Book", createIntent())
                .setAutoCancel(true)
                .build()

        private fun createIntent(group: ChapterGroup? = null) =
            PendingIntent.getActivity(
                context,
                hashCode() + group.hashCode(),
                Intent(context, MainActivity::class.java).apply {
                    putExtra("book", book.id)
                    putExtra("chapters", group?.link)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )!!

    }

}
