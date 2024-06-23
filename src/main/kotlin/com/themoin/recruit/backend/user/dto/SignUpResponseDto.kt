package com.themoin.recruit.backend.user.dto

import com.themoin.recruit.backend.user.`object`.UserType

data class SignUpResponseDto (
    val userId : String,
    val name : String,
    val idType : UserType
)