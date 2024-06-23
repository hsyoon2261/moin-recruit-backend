package com.themoin.recruit.backend.user.service.internal

import com.themoin.recruit.backend.common.exception.BadRequestException
import com.themoin.recruit.backend.common.util.Crypt
import com.themoin.recruit.backend.user.entity.User
import com.themoin.recruit.backend.user.repository.UserRepository

internal class UserCore(
    private val repository: UserRepository,
) {
    fun getDecryptedPassword(user: User): String {
        return Crypt.decrypt(user.password, user.userId)
    }

    fun validateDuplicateUser(userId: String) {
        if (repository.existsByUserId(userId)) {
            throw BadRequestException("User with user ID '$userId' already exists")
        }
    }
}