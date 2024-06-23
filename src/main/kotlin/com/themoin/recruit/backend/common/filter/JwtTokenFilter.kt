package com.themoin.recruit.backend.common.filter

import com.themoin.recruit.backend.common.`object`.JwtConstants
import com.themoin.recruit.backend.common.security.CustomUserDetails
import com.themoin.recruit.backend.common.security.JwtTokenProvider
import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.filter.OncePerRequestFilter

class JwtTokenFilter (
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(JwtConstants.AUTH_HEADER)
        return if (bearerToken != null && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            bearerToken.substring(JwtConstants.TOKEN_PREFIX.length)
        } else null
    }

    private fun getUserDetailsFromClaims(claims: Claims) : CustomUserDetails {
        val userId = claims["userId"] as String
        val name = claims["name"] as String
        val idType = claims["idType"] as String
        val authorities = listOf(SimpleGrantedAuthority(idType))
        return CustomUserDetails(userId, "", authorities, name, idType)
    }

    private fun

}