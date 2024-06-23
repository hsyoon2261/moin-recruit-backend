package com.themoin.recruit.backend.common.filter

import com.themoin.recruit.backend.common.exception.TokenExpiredException
import com.themoin.recruit.backend.common.exception.UnAuthorizedException
import com.themoin.recruit.backend.common.`object`.JwtConstants
import com.themoin.recruit.backend.common.security.CustomUserDetails
import com.themoin.recruit.backend.common.security.JwtTokenProvider
import com.themoin.recruit.backend.common.util.CacheKey
import com.themoin.recruit.backend.common.util.CacheTopic
import com.themoin.recruit.backend.common.util.CustomCache
import com.themoin.recruit.backend.common.util.mapper.Authorize
import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

class JwtTokenFilter (
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)
        if (token != null){
            val claims = jwtTokenProvider.getAuthentication(token)
            validateClaims(claims, request, response)
            val userDetails = getSimpleUserDetailsFromClaims(claims)
            val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = auth
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(JwtConstants.AUTH_HEADER)
        return if (bearerToken != null && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            bearerToken.substring(JwtConstants.TOKEN_PREFIX.length)
        } else null
    }

    private fun getSimpleUserDetailsFromClaims(claims: Claims) : CustomUserDetails {
        val userId = claims["userId"] as String
        val name = claims["name"] as String
        val idType = claims["idType"] as String
        val authorities = listOf(SimpleGrantedAuthority(idType))
        return CustomUserDetails(userId, "", authorities, name, idType)
    }

    private fun validateClaims(claims: Claims, request: HttpServletRequest, response : HttpServletResponse) {
        val isExpired = claims.expiration.before(Date())
        val sessionId = Authorize.getSession(request)
        val currentSession = CustomCache.get<CustomUserDetails>(CacheKey.SESSION_INFO, sessionId)
        val isSameSession = (currentSession != null)
        if (isExpired) {
            if (isSameSession == false)
                throw UnAuthorizedException("Token is expired")
            val refreshedToken = jwtTokenProvider.generateToken(currentSession!!)
            throw TokenExpiredException(refreshedToken);
        }

        if (isSameSession)
            return

        val newSessionId = Authorize.generateSessionId()
        val simpleDetails = getSimpleUserDetailsFromClaims(claims)
        CustomCache.set(CacheKey.SESSION_INFO, newSessionId, simpleDetails)
        CustomCache.publish(CacheTopic.FILL_SESSION_INFO, newSessionId)
        response.addCookie(Cookie("MOSESSIONID", newSessionId))
    }

}