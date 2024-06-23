package com.themoin.recruit.backend.user.entity

import com.themoin.recruit.backend.common.util.Crypt
import com.themoin.recruit.backend.user.dto.SignUpRequestDto
import com.themoin.recruit.backend.user.dto.SignUpResponseDto
import com.themoin.recruit.backend.user.`object`.UserType
import com.themoin.recruit.backend.user.util.isValidRegistrationNumber
import jakarta.persistence.*
import jakarta.validation.constraints.Email

@Entity
@Table(name = "user")
class User(
    @Id
    @Column(nullable = false, unique = true)
    @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)\$", message = "Email should be valid")
    var userId: String,
    @Column(nullable = false)
    val password: String,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val idType: UserType,
    @Column(nullable = false)
    val idValue: String,
) {
    init {
        val originValue = Crypt.decrypt(idValue, userId)
        require(isValidRegistrationNumber(originValue, idType)) {
            when (idType) {
                UserType.REG_NO -> "Invalid resident registration number"
                UserType.BUSINESS_NO -> "Invalid business registration number"
            }
        }
    }

    companion object {
        fun createUserSimple(signUpRequestDto: SignUpRequestDto): User {
            return User(
                userId = signUpRequestDto.userId,
                password = Crypt.encrypt(signUpRequestDto.password, signUpRequestDto.userId),
                name = signUpRequestDto.name,
                idType = signUpRequestDto.idType,
                idValue = Crypt.encrypt(signUpRequestDto.idValue, signUpRequestDto.userId)
            )
        }

        fun toResponseDto(savedUser: User): SignUpResponseDto {
            return SignUpResponseDto(
                userId = savedUser.userId,
                name = savedUser.name,
                idType = savedUser.idType
            )
        }
    }
}