package com.ubadahj.qidianundergroud.services

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.utils.isNetworkAvailable
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import java.util.concurrent.TimeUnit
import kotlin.Result.Companion as ResultKt

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

        if (!applicationContext.isNetworkAvailable()) {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(
                    0,
                    "No internet connection available".toErrorNotification(applicationContext)
                )
            }

            return Result.failure()
        }

        val failures = mutableListOf<Throwable>()
        getNotifications()
            .onCompletion {
                if (failures.isEmpty()) return@onCompletion
                with(NotificationManagerCompat.from(applicationContext)) {
                    notify(
                        failures.hashCode(),
                        failures
                            .joinToString("\n") { it.message.toString() }
                            .toErrorNotification(applicationContext)
                    )
                }
            }
            .collect {
                with(NotificationManagerCompat.from(applicationContext)) {
                    if (it.isSuccess) {
                        val notification = it.getOrThrow()
                        notify(notification.id, notification.createNotification())
                    } else {
                        failures.add(it.exceptionOrNull()!!)
                    }
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
                        ResultKt.success(
                            BookNotification(applicationContext, book, refreshedGroups, updateCount)
                        )
                    )
            } catch (e: Exception) {
                ResultKt.failure<BookNotification>(e)
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

    private fun String.toErrorNotification(context: Context) =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_image_outline)
            .setContentTitle("Error fetching updates")
            .setContentText(this)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()


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

        private fun createIntent(group: Group? = null) = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .run {
                if (group != null) setDestination(R.id.readerContainerFragment)
                    .setArguments(
                        bundleOf(
                            "bookId" to book.id,
                            "groupLink" to group.link
                        )
                    )
                else setDestination(R.id.bookFragment)
                    .setArguments(bundleOf("bookId" to book.id))
            }.createPendingIntent()

    }

}

fun WorkManager.launchBookUpdateService(context: Context, timeUnits: Pair<Long, TimeUnit>) {
    val uniqueTag = context.getString(R.string.worker_library_notification)
    enqueueUniquePeriodicWork(
        uniqueTag,
        ExistingPeriodicWorkPolicy.REPLACE,
        PeriodicWorkRequestBuilder<NotificationWorker>(
            timeUnits.first, timeUnits.second
        ).build()
    )
}