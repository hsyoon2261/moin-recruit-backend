package com.themoin.recruit.backend.user.`object`

enum class UserType(val pattern: String) {
    REG_NO("""\d{6}-\d{7}"""),
    BUSINESS_NO("""\d{3}-\d{2}-\d{5}""")
}