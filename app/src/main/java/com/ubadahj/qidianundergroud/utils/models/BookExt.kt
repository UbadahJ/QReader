package com.ubadahj.qidianundergroud.utils.models

import android.content.Context
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.repositories.MetadataRepository

suspend fun Book.setNotifications(context: Context, enable: Boolean) =
    MetadataRepository(context).setNotifications(this, enable)