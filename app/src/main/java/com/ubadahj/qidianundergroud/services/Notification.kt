package com.ubadahj.qidianundergroud.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

data class Channel(
    val name: String,
    val id: String,
    val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
)

fun createChannel(context: Context, channel: Channel) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = channel.name
        val importance = channel.importance
        // Register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(channel.id, name, importance)
        )
    }
}

suspend fun NotificationCompat.Builder.setLargeIcon(
    context: Context,
    data: Any?
): NotificationCompat.Builder {
    if (data != null) {
        val request = ImageRequest.Builder(context)
            .data(data)
            .transformations(CircleCropTransformation())
            .target { setLargeIcon(it.toBitmap()) }
            .build()

        ImageLoader(context).execute(request)
    }

    return this
}
