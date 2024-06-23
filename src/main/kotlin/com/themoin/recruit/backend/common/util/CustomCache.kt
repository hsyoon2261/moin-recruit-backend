package com.themoin.recruit.backend.common.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

val objectMapper = jacksonObjectMapper()
data class CacheItem(val value: String, var expiryTime: LocalDateTime)


object CustomCache {

    private val cache = ConcurrentHashMap<String, ConcurrentHashMap<String, CacheItem>>()
    private val subscribers = ConcurrentHashMap<String, MutableList<(String) -> Unit>>()


    fun set(key: CacheKey, field: String, value: String, expiryTerm: Long = 60*60*24): String? {
        val fieldMap = cache.computeIfAbsent(key.name) { ConcurrentHashMap() }
        val expiryTime = LocalDateTime.now().plusSeconds(expiryTerm)
        val previousItem = fieldMap.put(field, CacheItem(value, expiryTime))
        return previousItem?.value
    }

    fun get(key: CacheKey, field: String): String? {
        val item = cache[key.name]?.get(field)
        if (item != null && item.expiryTime.isAfter(LocalDateTime.now())) {
            return item.value
        }
        return null
    }

    fun consume(key: CacheKey, field: String): String? {
        val item = cache[key.name]?.remove(field)
        if (item != null && item.expiryTime.isAfter(LocalDateTime.now())) {
            return item.value
        }
        return null
    }

    inline fun <reified T> set(key: CacheKey, field: String, value: T, expiryTerm: Long = 1): T? {
        val jsonValue = objectMapper.writeValueAsString(value)
        val previousValueJson = set(key, field, jsonValue, expiryTerm)
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

    fun subscribe(topic : CacheTopic, callback: (String) -> Unit) {
        val list = subscribers.getOrPut(topic.name) { mutableListOf() }
        list.add(callback)
    }

    fun publish(topic: CacheTopic, payload: String) {
        subscribers[topic.name]?.forEach { it(payload) }
    }
}
enum class CacheKey {
    SESSION_INFO,
    TOKEN_REFRESH,
}

enum class CacheTopic(val description: String) {
    FILL_SESSION_INFO("payload is sessionId string")
}