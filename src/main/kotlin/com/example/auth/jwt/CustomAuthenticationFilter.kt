package com.example.auth.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.example.auth.dto.LoginRequest
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.io.IOException


@RequiredArgsConstructor
class CustomAuthenticationFilter(private val authenticationManager: AuthenticationManager, private val jwtProvider: JwtProvider) :
    AbstractAuthenticationProcessingFilter(DEFAULT_REQUEST_MATCHER) {
    private val objectMapper = ObjectMapper()

    @Throws(AuthenticationException::class, IOException::class, ServletException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val user = objectMapper.readValue(request.inputStream, LoginRequest::class.java)
        val authenticationToken = UsernamePasswordAuthenticationToken(user.name, user.password)
        return authenticationManager.authenticate(authenticationToken)
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication) {
        response.addHeader("Authorization", "Bearer ${jwtProvider.createToken(authResult.name)}")
    }

    companion object {
        private val DEFAULT_REQUEST_MATCHER = AntPathRequestMatcher("/api/v1/users/login", "POST")
    }
}