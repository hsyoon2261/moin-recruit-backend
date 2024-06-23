package com.themoin.recruit.backend.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto (
    @field:NotBlank
    @field:Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)\$", message = "Invalid email format")
    val userId: String,

    @field:NotBlank
    val password: String
)