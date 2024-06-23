package com.themoin.recruit.backend.user.dto

import com.themoin.recruit.backend.common.annotation.ValidEnum
import com.themoin.recruit.backend.user.annotation.ValidIdValue
import com.themoin.recruit.backend.user.`object`.UserType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignUpRequestDto(

    @field:NotBlank
    @field:Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)\$", message = "Invalid email format")
    val userId: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    @ValidEnum(enumClass = UserType::class, message = "Invalid user type")
    val idType: UserType,

    @field:NotBlank
    @ValidIdValue(message = "Invalid registration number format")
    val idValue: String,

)

