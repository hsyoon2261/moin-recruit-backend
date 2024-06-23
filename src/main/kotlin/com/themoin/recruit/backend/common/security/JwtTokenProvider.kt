package com.themoin.recruit.backend.common.security

import com.themoin.recruit.backend.common.exception.InvalidTokenException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long,
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(userDetails: UserDetails): String {
        var claims = Jwts.claims().apply {
            this["userId"] = userDetails.username
            this["name"] = (userDetails as CustomUserDetails).name
            this["idType"] = userDetails.idType
        }

        val now = Date()
        val validity = Date(now.time + expiration)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey)
            .compact()
    }

    fun parseToken(token: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: ExpiredJwtException) {
            e.claims
        } catch (e: Exception) {
            throw InvalidTokenException("Invalid token")
        }
    }

    fun getAuthentication(token: String): Claims {
        return parseToken(token)
    }
}