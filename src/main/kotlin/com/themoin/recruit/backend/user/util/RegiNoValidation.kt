package com.themoin.recruit.backend.user.util

import com.themoin.recruit.backend.user.`object`.UserType

fun isValidResidentRegistrationNumber(input: String): Boolean {
    val regex = Regex("""\d{6}-\d{7}""")
    return regex.matches(input)
}

fun isValidBusinessRegistrationNumber(input: String): Boolean {
    val regex = Regex("""\d{3}-\d{2}-\d{5}""")
    return regex.matches(input)
}

fun isValidRegistrationNumber(input: String, type: UserType): Boolean {
    val regex = Regex(type.pattern)
    return regex.matches(input)
}