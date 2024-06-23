package com.themoin.recruit.backend.common.aspect

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.themoin.recruit.backend.common.annotation.Logging
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

val objectMapper = jacksonObjectMapper()
val logger: Log =  LogFactory.getLog(LoggingAspect::class.java)

@Aspect
@Component
class LoggingAspect {
    @Around("@annotation(logging)")
    fun logProcess(joinPoint: ProceedingJoinPoint, logging:Logging): Any? {
        val startTime = System.currentTimeMillis()
        val method = joinPoint.signature as MethodSignature
        val className = method.method.declaringClass.name
        val methodName = method.method.name

        val arguments = joinPoint.args.map { objectMapper.writeValueAsString(it) }

        return try {
            val result = joinPoint.proceed()
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            val resultJson = objectMapper.writeValueAsString(result)

            val logInfo = mapOf(
                "MethodInfo" to "$className.$methodName",
                "Request" to arguments,
                "Response" to resultJson,
                "ExecutionTime" to "${duration}ms"
            )
            logger.info(objectMapper.writeValueAsString(logInfo))
            result
        } catch (ex: Throwable) {
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            val logInfo = mapOf(
                "MethodInfo" to "$className.$methodName",
                "Request" to arguments,
                "Error" to ex.toString(),
                "ExecutionTime" to "${duration}ms"
            )
            logger.error(objectMapper.writeValueAsString(logInfo))
            throw ex
        }
    }
}