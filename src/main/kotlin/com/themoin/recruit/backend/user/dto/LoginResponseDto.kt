package com.themoin.recruit.backend.user.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class LoginResponseDto(
    val jwtToken: String
)