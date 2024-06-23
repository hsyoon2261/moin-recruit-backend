package com.themoin.recruit.backend.common.util

import kotlinx.coroutines.runBlocking

fun <T> (suspend () -> T).sync(): T {
    var result: T
    runBlocking {
        result = this@sync.invoke()
    }
    return result
}