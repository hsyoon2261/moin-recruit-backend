package com.themoin.recruit.backend.common.dto

import com.themoin.recruit.backend.common.`object`.ResultCode
import org.springframework.http.ResponseEntity

typealias Res<T> = ResponseEntity<Response<T>>

data class Response<T>(
    val code: ResultCode,
    val subcode: String? = null,
    val message: String? = null,
    val data: T? = null,
) {
    constructor(resultCode: ResultCode, message: String? = null, data: T? = null) : this(
        code = resultCode,
        subcode = resultCode.description,
        message = message,
        data = data
    )

    fun toResponseEntity(): ResponseEntity<Response<T>> {
        return ResponseEntity(this, code.httpStatus)
    }
}
