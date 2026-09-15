package com.bilal.ktornetworking.bridge

import com.bilal.ktornetworking.domain.model.News
import com.bilal.ktornetworking.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

/**
 * Swift-facing access to the shared repository. Start Koin before creating this client.
 * Use from the main thread; callbacks run on the main dispatcher.
 * Call close when the owning Swift model is disposed. A closed client cannot be reused.
 */
class IosNewsClient internal constructor(
    private val repository: NewsRepository,
    private val scope: CoroutineScope,
) {
    constructor() : this(KoinPlatform.getKoin().get<NewsRepository>(), MainScope())

    fun getNews(onSuccess: (List<News>) -> Unit, onError: () -> Unit): NewsRequest =
        launchRequest(load = { repository.getNews() }, onSuccess = onSuccess, onError = onError)

    fun getNewsById(newsId: Int, onSuccess: (News) -> Unit, onError: () -> Unit): NewsRequest =
        launchRequest(
            load = { repository.getNewsById(newsId) },
            onSuccess = onSuccess,
            onError = onError,
        )

    fun close() {
        scope.cancel()
    }

    private fun <T> launchRequest(
        load: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: () -> Unit,
    ): NewsRequest {
        val job = scope.launch {
            val result = try {
                load()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                ensureActive()
                onError()
                return@launch
            }
            ensureActive()
            onSuccess(result)
        }
        return NewsRequest(job)
    }
}
