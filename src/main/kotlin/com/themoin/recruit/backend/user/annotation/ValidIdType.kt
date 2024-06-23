package com.themoin.recruit.backend.user.annotation

import com.themoin.recruit.backend.user.util.isValidBusinessRegistrationNumber
import com.themoin.recruit.backend.user.util.isValidResidentRegistrationNumber
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [ValidIdValueValidator::class])
annotation class ValidIdValue(
    val message: String = "Invalid id value format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)


class ValidIdValueValidator : ConstraintValidator<ValidIdValue, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true
        }
        return isValidResidentRegistrationNumber(value) || isValidBusinessRegistrationNumber(value)
    }
}

