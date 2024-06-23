package com.themoin.recruit.backend.common.aspect

import com.themoin.recruit.backend.common.annotation.UserSensitive
import com.themoin.recruit.backend.common.framework.SyncExecutor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.annotation.AnnotationUtils.findAnnotation
import org.springframework.util.ReflectionUtils
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import kotlin.reflect.jvm.kotlinFunction


interface HasUserId { val userId: String }
annotation class UserId
val parser = SpelExpressionParser()

@Aspect
@Component
class UserSensitiveAspect(
    val executor: SyncExecutor,
    private val eventHandler: ApplicationEventPublisher,
) {

    companion object {
        var methodCache = mutableMapOf<String, String>()
    }

    @Around("@annotation(userSensitive)")
    fun handleUserSensitiveMethods(joinPoint: ProceedingJoinPoint, userSensitive: UserSensitive): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val userId = methodCache[method.name]?.let {
            extractUserIdFromArgs(joinPoint.args, it)
        } ?: return joinPoint.proceed()

        return if (method.isSuspendFunction()) {
            GlobalScope.launch {
                executor.execute(topic = userId) {
                    joinPoint.proceed() as Any
                }
            }
            null
        } else {
            runBlocking {
                executor.execute(topic = userId) {
                    joinPoint.proceed() as Any
                }
            }
        }
    }

    fun validate() {
        val beans = (eventHandler as ListableBeanFactory).getBeansOfType(Any::class.java)

        beans.values.forEach { bean ->
            ReflectionUtils.doWithMethods(bean.javaClass) { method ->
                val userSensitive = findAnnotation(method, UserSensitive::class.java)
                if (userSensitive != null) {
                    val userIdParam = extractUserIdParam(method)
                    if (userIdParam == null) {
                        println("No userId found for method: ${method.name}")
                        throw IllegalStateException("No userId found for @UserSensitive annotated method: ${method.name}")
                    } else {
                        methodCache[method.name] = userIdParam
                    }
                }
            }
        }
    }

    private fun extractUserIdParam(method: Method): String? {
        val parameterAnnotations = method.parameterAnnotations
        parameterAnnotations.forEachIndexed { index, annotations ->
            annotations.forEach { annotation ->
                if (annotation is UserId) {
                    return method.parameters[index].name
                }
            }
        }
        method.parameters.forEach { parameter ->
            if (HasUserId::class.java.isAssignableFrom(parameter.type)) {
                return parameter.name
            }
        }
        return null
    }

    private fun extractUserIdFromArgs(args: Array<Any>, paramName: String): String? {
        args.forEach { arg ->
            if (arg is HasUserId) {
                return arg.userId
            }
        }
        return args.firstOrNull { it::class.java.simpleName == paramName }?.toString()
    }

    private fun Method.isSuspendFunction(): Boolean {
        return this.kotlinFunction?.isSuspend == true
    }
}

