package com.themoin.recruit.backend.user.service

import com.themoin.recruit.backend.common.exception.BadRequestException
import com.themoin.recruit.backend.user.dto.LoginRequestDto
import com.themoin.recruit.backend.user.dto.LoginResponseDto
import com.themoin.recruit.backend.user.dto.SignUpRequestDto
import com.themoin.recruit.backend.user.dto.SignUpResponseDto
import com.themoin.recruit.backend.user.entity.User
import com.themoin.recruit.backend.user.repository.UserRepository
import com.themoin.recruit.backend.user.service.internal.UserCore
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    private val core = UserCore(userRepository)

    fun register(signUpRequestDto: SignUpRequestDto): SignUpResponseDto {
        core.validateDuplicateUser(signUpRequestDto.userId)
        val user = User.createUserSimple(signUpRequestDto)
        val savedUser = userRepository.save(user)
        return User.toResponseDto(savedUser)
    }

    fun login(loginRequestDto: LoginRequestDto): LoginResponseDto {
        val user = userRepository.findByUserId(loginRequestDto.userId)
            ?: throw BadRequestException("User with user ID '${loginRequestDto.userId}' does not exist")

        val decryptedPassword = core.getDecryptedPassword(user)
        if (decryptedPassword != loginRequestDto.password)
            throw BadRequestException("Password is incorrect")

        val token = jwtTokenProvider.generateToken(core.getCustomUserDetails(user));
        return LoginResponseDto(jwtToken = token)
    }
}