package com.themoin.recruit.backend.common.framework

import kotlinx.coroutines.*
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

private const val timeoutMin = 60L

@Component
class SyncExecutor {
    private val scopes = ConcurrentHashMap<String, CoroutineScope>()
    private val timeoutDuration = TimeUnit.MINUTES.toMillis(timeoutMin)

    private fun getOrCreateScope(topic: String): CoroutineScope =
        scopes.computeIfAbsent(topic) {
            CoroutineScope(Dispatchers.Default + SupervisorJob()).also { scope ->
                setupTimeout(topic, scope)
            }
        }

    private fun setupTimeout(topic: String, scope: CoroutineScope) {
        scope.launch {
            try {
                delay(timeoutDuration)
                releaseResources(topic)
            } catch (e: CancellationException) {
            }
        }
    }

    suspend fun <T> execute(topic: String, task: suspend () -> T): T {
        val scope = getOrCreateScope(topic)
        return scope.async {
            task()
        }.await()
    }

    fun releaseResources(topic: String) {
        scopes[topic]?.cancel()
        scopes.remove(topic)
    }
}


