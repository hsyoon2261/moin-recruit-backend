package com.themoin.recruit.backend.common.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

val logger: Log = LogFactory.getLog(RequestResponseLoggingInterceptor::class.java)


@Component
class RequestResponseLoggingInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val loggerMsg = buildCurlCommand(request)
        logger.info(loggerMsg)
        return true
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        val cachingResponse = response as ContentCachingResponseWrapper
        val responseBody = String(cachingResponse.contentAsByteArray, Charsets.UTF_8)
        logger.info("Response: ${response.status} Body: $responseBody")
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        logger.info("Completed: ${request.method} ${request.requestURI} with status ${response.status}")
    }

    private fun buildCurlCommand(request: HttpServletRequest): String {
        val headers = StringBuilder()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            val headerValue = request.getHeader(headerName)
            headers.append("-H '$headerName: $headerValue' ")
        }

        val method = request.method
        val requestUri = request.requestURI
        val queryString = request.queryString
        val fullUrl = if (queryString == null) requestUri else "$requestUri?$queryString"

        val body = getBody(request)

        return "curl -X $method $headers '$fullUrl' $body"
    }

    private fun getBody(request: HttpServletRequest): String {
        val cachedRequest = request as ContentCachingRequestWrapper
        val body = cachedRequest.contentAsByteArray
        return if (body.isNotEmpty()) "--data '${String(body)}'" else ""
    }
}