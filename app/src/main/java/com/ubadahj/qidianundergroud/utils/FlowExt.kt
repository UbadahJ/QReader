package com.ubadahj.qidianundergroud.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

suspend inline fun <T> Flow<T?>.collectNotNull(crossinline action: suspend (value: T) -> Unit) =
    collect { if (it != null) action(it) }