package com.ubadahj.qidianundergroud.utils.coroutines

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> MutableStateFlow<T>.asSourceFlow() = SourceStateFlow(this)

class SourceStateFlow<T>(
    private val flow: MutableStateFlow<T>
) : MutableStateFlow<T> by flow {
    private var job: Job? = null

    suspend fun emitAll(flow: Flow<T>) {
        job?.cancel()
        coroutineScope {
            job = launch {
                ensureActive()
                flow.collect { emit(it) }
            }
        }
    }
}