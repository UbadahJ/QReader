package com.ubadahj.qidianundergroud.services

import android.content.Context
import android.webkit.WebView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.e
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.repositories.BookRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlin.math.abs
import kotlin.random.Random

private const val DOWNLOADER_ID: String = "69"

@HiltWorker
class DownloadService @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val bookRepo: BookRepository
) : CoroutineWorker(context, params) {

    private val notificationId = 69420

    override suspend fun doWork(): Result {
        val book = bookRepo.getBookById(inputData.getString("book_id")!!).first()
        val groups = bookRepo.getGroups(book).first()

        createChannel(
            applicationContext, Channel(
                name = applicationContext.getString(R.string.downloader_name),
                id = DOWNLOADER_ID
            )
        )

        val builder = NotificationCompat.Builder(applicationContext, DOWNLOADER_ID).apply {
            setContentTitle("Downloading ${book.name}")
            setSmallIcon(R.drawable.download)
            setOnlyAlertOnce(true)
            priority = NotificationCompat.PRIORITY_LOW
        }

        NotificationManagerCompat.from(applicationContext).apply {
            builder.setProgress(groups.size, 0, false)
            notify(notificationId, builder.build())

            try {
                bookRepo.download(
                    book,
                    {
                        WebView(it).apply { settings.javaScriptEnabled = true }
                    }
                ).flowOn(Dispatchers.Main)
                    .collectIndexed { i, group ->
                        builder.setContentText(group.text)
                        builder.setProgress(groups.size, i, false)
                        notify(notificationId, builder.build())
                    }
            } catch (e: Exception) {
                e(e) { "getNotifications: Failed to download $book" }
                notify(
                    abs(Random.nextInt()),
                    NotificationCompat.Builder(applicationContext, DOWNLOADER_ID).apply {
                        setContentTitle("Failed to download ${book.name}")
                        setSmallIcon(R.drawable.download)
                        priority = NotificationCompat.PRIORITY_DEFAULT
                    }.build()
                )
                builder.setProgress(0, 0, false)
                cancel(notificationId)
                return Result.failure()
            }

            builder.setProgress(0, 0, false)
            cancel(notificationId)

            notify(
                abs(Random.nextInt()),
                NotificationCompat.Builder(applicationContext, DOWNLOADER_ID).apply {
                    setContentTitle("${book.name} downloaded")
                    setSmallIcon(R.drawable.download)
                    priority = NotificationCompat.PRIORITY_DEFAULT
                }.build()
            )
        }

        return Result.success()
    }

}
