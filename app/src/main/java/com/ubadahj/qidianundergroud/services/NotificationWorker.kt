package com.ubadahj.qidianundergroud.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.e
import com.ubadahj.qidianundergroud.MainActivity
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

private const val CHANNEL_ID: String = "42000"

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val bookRepo: BookRepository,
    private val groupRepo: GroupRepository,
) : CoroutineWorker(context, params) {

    private val notificationId = 42069

    override suspend fun doWork(): Result {
        createChannel(
            applicationContext, Channel(
                name = applicationContext.getString(R.string.channel_name),
                id = CHANNEL_ID
            )
        )
        getNotifications().collect {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(it.id, it.createNotification())
            }
        }
        return Result.success()
    }

    private fun getNotifications() = flow {
        val books = bookRepo.getLibraryBooks().first()

        progressNotification(books) { book ->
            try {
                val lastGroup = groupRepo.getGroups(book).first()
                val refreshedGroups = groupRepo.getGroups(book, true).first()
                val updateCount = refreshedGroups.lastChapter() - lastGroup.lastChapter()
                if (updateCount > 0)
                    emit(
                        BookNotification(
                            applicationContext, book, refreshedGroups, updateCount
                        )
                    )
            } catch (e: Exception) {
                e(e) { "getNotifications: Failed to query $book" }
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun List<Group>.lastChapter(): Int {
        return maxByOrNull { it.lastChapter }?.lastChapter?.toInt() ?: 0
    }

    private suspend fun progressNotification(
        books: List<Book>,
        action: suspend (Book) -> Unit
    ) {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
            setContentTitle("Checking for updates")
            setSmallIcon(R.drawable.app_image_outline)
            setOnlyAlertOnce(true)
            priority = NotificationCompat.PRIORITY_LOW
        }

        NotificationManagerCompat.from(applicationContext).apply {
            builder.setProgress(books.size, 0, false)
            notify(notificationId, builder.build())

            books.onEachIndexed { counter, book ->
                builder.apply {
                    setContentText(book.name)
                    setProgress(books.size, counter, false)
                    notify(notificationId, build())
                }

                action(book)
            }

            builder.setProgress(0, 0, false)
            cancel(notificationId)
        }
    }

    private data class BookNotification(
        val context: Context,
        val book: Book,
        val groups: List<Group>,
        val updateCount: Int
    ) {

        private val lastGroup = groups.maxByOrNull { it.lastChapter }!!
        private val text = "$updateCount new chapter${if (updateCount > 1) "s" else ""} available"

        private val groupKey = "com.ubadahj.qidianunderground.CHAPTER_UPDATES"

        val id: Int = book.id

        suspend fun createNotification(): Notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_image_outline)
                .setContentTitle(book.name)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(createIntent(lastGroup))
                .addAction(R.drawable.add, "Open Book", createIntent())
                .setAutoCancel(true)
                .setLargeIcon(context, book.coverPath)
                .setGroup(groupKey)
                .build()

        private fun createIntent(group: Group? = null) =
            PendingIntent.getActivity(
                context,
                hashCode() + group.hashCode(),
                Intent(context, MainActivity::class.java).apply {
                    putExtra("book", book.id)
                    putExtra("group", group?.link)
                },
                flags
            )!!

    }

}
