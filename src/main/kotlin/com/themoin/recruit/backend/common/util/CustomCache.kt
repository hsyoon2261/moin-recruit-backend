package com.themoin.recruit.backend.common.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.concurrent.ConcurrentHashMap

val objectMapper = jacksonObjectMapper()
val cache = ConcurrentHashMap<String, ConcurrentHashMap<String, String>>()


object CustomCache {

    fun set(key: CacheKey, field: String, value: String): String? {
        val fieldMap = cache.computeIfAbsent(key.name) { ConcurrentHashMap() }
        return fieldMap.put(field, value)
    }

    fun get(key: CacheKey, field: String): String? {
        return cache[key.name]?.get(field)
    }

    fun consume(key: CacheKey, field: String): String? {
        return cache[key.name]?.remove(field)
    }

    inline fun <reified T> set(key: CacheKey, field: String, value: T): T? {
        val jsonValue = objectMapper.writeValueAsString(value)
        val previousValueJson = set(key, field, jsonValue)
        return previousValueJson?.let { objectMapper.readValue<T>(it) }
    }

    inline fun <reified T> get(key: CacheKey, field: String): T? {
        val jsonValue = get(key, field)
        return jsonValue?.let { objectMapper.readValue<T>(it) }
    }

    inline fun <reified T> consume(key: CacheKey, field: String): T? {
        val jsonValue = consume(key, field)
        return jsonValue?.let { objectMapper.readValue<T>(it) }
    }
}

enum class CacheKey {
    SESSION_INFO,
    TOKEN_REFRESH,
}