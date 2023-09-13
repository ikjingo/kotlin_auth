package com.example.auth.jwt

import com.example.auth.model.User
import com.example.auth.repository.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Repository


@RequiredArgsConstructor
@Repository
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(name: String): UserDetails {
        val user: User = userRepository.findUserByName(name)

        return org.springframework.security.core.userdetails.User(
            user.name,
            user.password,
            listOf(SimpleGrantedAuthority("USER"))
        )
    }
}