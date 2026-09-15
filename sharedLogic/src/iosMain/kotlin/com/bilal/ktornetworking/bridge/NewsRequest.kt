package com.bilal.ktornetworking.bridge

import kotlinx.coroutines.Job

/** Cancels an outstanding request. Cancellation does not call onError. */
class NewsRequest internal constructor(private val job: Job) {
    fun cancel() {
        job.cancel()
    }
}
