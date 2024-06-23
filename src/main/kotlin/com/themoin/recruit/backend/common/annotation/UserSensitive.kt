package com.themoin.recruit.backend.common.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserSensitive(val userId: String = "")