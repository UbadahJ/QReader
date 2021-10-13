package com.ubadahj.qidianundergroud.services.updater.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ubadahj.qidianundergroud.BuildConfig
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.services.Channel
import com.ubadahj.qidianundergroud.services.createChannel
import com.ubadahj.qidianundergroud.services.updater.api.UpdateApi
import com.ubadahj.qidianundergroud.services.updater.models.Release
import com.ubadahj.qidianundergroud.services.updater.models.Version
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val UPDATE_ID: String = "86"

@HiltWorker
class UpdateService @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val api: UpdateApi
) : CoroutineWorker(context, params) {

    private val notificationId = 42690

    override suspend fun doWork(): Result {
        createChannel(
            applicationContext, Channel(
                name = applicationContext.getString(R.string.update_notification),
                id = UPDATE_ID
            )
        )

        val release = api.checkRelease()
        val version = Version.create(release.tagName)
        val current = Version.create(BuildConfig.VERSION_NAME)
        if (version > current) {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(notificationId, createNotification(current, version, createIntent(release)))
            }
        }

        return Result.success()
    }

    private fun createNotification(old: Version, new: Version, intent: PendingIntent) =
        NotificationCompat.Builder(context, UPDATE_ID)
            .setSmallIcon(R.drawable.app_image_outline)
            .setContentTitle("Update available - ${new.simpleText}")
            .setContentText("Download new version to update from ${old.simpleText}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
            .build()

    private fun createIntent(release: Release) = PendingIntent.getActivity(
        context,
        release.hashCode(),
        Intent(Intent.ACTION_VIEW, release.htmlUrl.toUri()),
        PendingIntent.FLAG_CANCEL_CURRENT
    )

    private val Version.simpleText: String
        get() = "${version}b$build ($branch)"

}