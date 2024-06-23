package com.themoin.recruit.backend.common.util.mapper

import jakarta.servlet.http.HttpServletRequest
import java.util.*

object Authorize {

    fun getSession(request: HttpServletRequest): String {
        val cookies = request.cookies
        val session = cookies?.firstOrNull { it.name == "MOSESSIONID" }?.value
        return session ?: ""
    }

    fun generateSessionId(): String {
        return UUID.randomUUID().toString()
    }
}