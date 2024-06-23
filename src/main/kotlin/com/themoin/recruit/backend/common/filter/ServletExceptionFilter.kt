package com.themoin.recruit.backend.common.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.themoin.recruit.backend.common.dto.Response
import com.themoin.recruit.backend.common.exception.ExceptionBase
import com.themoin.recruit.backend.common.`object`.ResultCode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

class ServletExceptionFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: ExceptionBase) {
            sendError(response, e.resultCode, e.message)
        } catch (e: Exception) {
            sendError(response, ResultCode.INTERNAL_SERVER_ERROR, e.message)
        }

    }

    private fun sendError(res: HttpServletResponse, errorCode: ResultCode, message: String?) {
        val errorResponse = Response<Unit>(errorCode, message = message)
        val responseString = objectMapper.writeValueAsString(errorResponse)
        res.characterEncoding = "UTF-8"
        res.status = errorCode.httpStatus.value()
        res.contentType = MediaType.APPLICATION_JSON_VALUE
        res.writer.write(responseString)
    }
}