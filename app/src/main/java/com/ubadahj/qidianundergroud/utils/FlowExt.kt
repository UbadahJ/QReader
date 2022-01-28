package com.ubadahj.qidianundergroud.utils

import com.ubadahj.qidianundergroud.R
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toCollection

suspend inline fun <T> Flow<T?>.collectNotNull(crossinline action: suspend (value: T) -> Unit) =
    collect { if (it != null) action(it) }

suspend inline fun <T, R> Flow<T>.parallelMap(
    max: Int,
    crossinline action: suspend (value: T) -> R
) = coroutineScope {
    val list = toCollection(mutableListOf())
    list.chunked(list.size / max)
        .map { async { it.map { action(it) } } }
        .map { it.await() }
        .flatten()
}

suspend inline fun <T, R> Flow<T>.parallelForEach(
    max: Int,
    crossinline action: suspend (value: T) -> R
) = coroutineScope {
    val list = toCollection(mutableListOf())
    list.chunked(list.size / max)
        .map { async { it.map { action(it) } } }
        .forEach { it.await() }
}