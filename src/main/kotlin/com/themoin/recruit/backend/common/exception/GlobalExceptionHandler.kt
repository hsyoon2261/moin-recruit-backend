package com.themoin.recruit.backend.common.exception

import com.themoin.recruit.backend.common.dto.Response
import com.themoin.recruit.backend.common.`object`.ResultCode
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ExceptionBase::class)
    fun handleBasicException(ex: ExceptionBase): ResponseEntity<Response<Unit>> {
        val response = Response<Unit>(ex.resultCode, message = ex.message)
        return ResponseEntity(response, ex.resultCode.httpStatus)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Response<String>> {
        val errors = ex.bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Invalid value" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Response(ResultCode.NOT_VALID, message = "Validation failed: $errors"))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptions(ex: ConstraintViolationException): ResponseEntity<Response<String>> {
        val errors = ex.constraintViolations.joinToString(", ") { it.message }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Response(ResultCode.FAILURE, message = "Validation failed: $errors"))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Response<Unit>> {
        val response = Response<Unit>(ResultCode.FAILURE, message = ex.message)
        return ResponseEntity(response, ResultCode.FAILURE.httpStatus)
    }
}
