package com.themoin.recruit.backend.user.repository

import com.themoin.recruit.backend.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository: JpaRepository<User, String> {
    fun existsByUserId(userId: String): Boolean
    fun findByUserId(userId: String): User?
}