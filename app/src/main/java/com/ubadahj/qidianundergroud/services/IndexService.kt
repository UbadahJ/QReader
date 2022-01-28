package com.ubadahj.qidianundergroud.services

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.e
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.MetadataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlin.math.abs
import kotlin.random.Random

private const val INDEX_ID: String = "86"

@HiltWorker
class IndexService @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val bookRepo: BookRepository,
    private val metaRepo: MetadataRepository,
) : CoroutineWorker(context, params) {

    private val notificationId = 86420

    override suspend fun doWork(): Result {
        val books = bookRepo.getUndergroundBooks().first()
        createChannel(
            applicationContext, Channel(
                name = applicationContext.getString(R.string.index_name),
                id = INDEX_ID,
                importance = NotificationManager.IMPORTANCE_LOW
            )
        )

        val builder = NotificationCompat.Builder(applicationContext, INDEX_ID).apply {
            setContentTitle("Generating indexes")
            setSmallIcon(R.drawable.download)
            setOnlyAlertOnce(true)
            priority = NotificationCompat.PRIORITY_LOW
        }

        NotificationManagerCompat.from(applicationContext).apply {
            builder.setProgress(books.size, 0, false)
            notify(notificationId, builder.build())

            try {
                coroutineScope {
                    books.filter { it.author == null }
                        .asFlow()
                        .flowOn(Dispatchers.IO)
                        .flatMapMerge(12) { flow { emit(it.apply { metaRepo.getBook(this) }) } }
                        .collectIndexed { i, it ->
                            builder.setContentText("[${i}/${books.size}] ${it.name}")
                            builder.setProgress(books.size, i, false)
                            notify(notificationId, builder.build())
                        }
                }
            } catch (e: Exception) {
                e(e) { "getNotifications: Failed generating index" }
                notify(
                    abs(Random.nextInt()),
                    NotificationCompat.Builder(applicationContext, INDEX_ID).apply {
                        setContentTitle("Failed to generate full index")
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
                NotificationCompat.Builder(applicationContext, INDEX_ID).apply {
                    setContentTitle("Index generated")
                    setSmallIcon(R.drawable.download)
                    priority = NotificationCompat.PRIORITY_DEFAULT
                }.build()
            )
        }

        return Result.success()
    }

}
