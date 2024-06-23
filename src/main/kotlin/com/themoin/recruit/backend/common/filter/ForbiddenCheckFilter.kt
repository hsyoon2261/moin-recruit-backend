package com.themoin.recruit.backend.common.filter

import com.themoin.recruit.backend.common.exception.NotFoundException
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExecutionChain
import org.springframework.web.servlet.HandlerMapping
import java.util.concurrent.ConcurrentHashMap

private val urlCache = ConcurrentHashMap<String, Boolean>()

@Component
class ForbiddenCheckFilter(
    @Qualifier("requestMappingHandlerMapping") private val handlerMapping: HandlerMapping
) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val uri = httpRequest.requestURI

        val isValid = if (uri.startsWith("/swagger-ui/")) {
            true
        } else {
            urlCache.computeIfAbsent(uri) { key ->
                val handlerExecutionChain: HandlerExecutionChain? = handlerMapping.getHandler(httpRequest)
                handlerExecutionChain != null
            }
        }

        if (isValid) {
            chain.doFilter(request, response)
        } else {
            throw NotFoundException("요청하신 페이지를 찾을 수 없습니다.")
        }
    }

    override fun destroy() {
        urlCache.clear()
    }
}
