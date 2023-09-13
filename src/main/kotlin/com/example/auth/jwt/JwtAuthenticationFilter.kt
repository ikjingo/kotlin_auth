package com.example.auth.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter() : OncePerRequestFilter() {
    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var userDetailsService: UserDetailsServiceImpl

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val path = request.requestURI
        if (path.equals("/api/v1/users/register") || path.equals("/api/v1/users/login")) {
            filterChain.doFilter(request, response)
            return
        }

        val auth: String? = request.getHeader("Authorization")
        if (auth.isNullOrEmpty()) {
            filterChain.doFilter(request, response)
            return
        }

        val token: String = auth.removePrefix("Bearer ").trim()
        if (jwtProvider.validateToken(token)) {
            val name = jwtProvider.getUserNameFromToken(token)
            val userDetails = userDetailsService.loadUserByUsername(name)
            SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        }

        filterChain.doFilter(request, response)
    }


}