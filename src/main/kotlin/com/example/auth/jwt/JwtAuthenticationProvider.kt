package com.example.auth.jwt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component


@Component
class JwtAuthenticationProvider(private val userDetailsService: UserDetailsServiceImpl) : AuthenticationProvider {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder
    override fun authenticate(authentication: Authentication): Authentication {
        val name = authentication.name
        val password = authentication.credentials as String
        val user = userDetailsService.loadUserByUsername(name)

        if (!this.passwordEncoder.matches(password, user.password)) {
            throw BadCredentialsException("password is not matches");
        }

        return UsernamePasswordAuthenticationToken(
            name,
            null,
            user.authorities
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}