package com.themoin.recruit.backend.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(HttpMethod.POST, "/api/accounts/register").permitAll() // 해당 엔드포인트 허용
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/configuration/**",
                        "/test/**",
                        "/redis-test/**",
                        "/api/accounts/signin/**",
                        "/api/accounts/oauth/**",
                        "/menus/**",
                    ).permitAll()
                    .anyRequest().authenticated()
            }.formLogin { it.disable() }
            .exceptionHandling { exceptions ->
            }
            .csrf { it.disable() }
            .httpBasic { it.disable() }

        return http.build()
    }
}