package com.themoin.recruit.backend.common.initialize

import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component

@Component
class Initializer(
    private val eventHandler: ApplicationEventPublisher,
    private val context: ConfigurableApplicationContext,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        try {
            initialize()
        } catch (e: Exception) {
            context.close()
            throw e
        }
    }

    private fun initialize() {
        validate()
        applyServerSource()
        initializeData()
        initializeCache()
    }


    private fun validate() {
        validateAOP()
    }

    private fun applyServerSource() {
    }

    private fun initializeCache() {
    }

    private fun initializeData() {
    }

    private fun validateAOP() {
        val beans = (eventHandler as ListableBeanFactory).getBeansOfType(Any::class.java)
        beans.values.forEach { bean ->
            if (bean::class.java.isAnnotationPresent(Aspect::class.java)) {
                try {
                    val method = bean::class.java.getMethod("validate")
                    method.invoke(bean)
                } catch (_: NoSuchMethodException) {
                } catch (e: Exception) {
                    println("Exception in validating AOP: ${e.message}")
                    context.close()
                    throw e
                }
            }
        }
    }
}