package com.ubadahj.qidianundergroud.utils.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.launch as coLaunch

class SingleJobScope(scope: CoroutineScope) : CoroutineScope by scope {

    private var job: Job? = null

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        job?.cancel()
        job = coLaunch(context, start, block)
    }

}