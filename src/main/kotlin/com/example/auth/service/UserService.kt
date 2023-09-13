package com.example.auth.service

import com.example.auth.dto.RegisterRequest
import com.example.auth.dto.RegisterResponse
import com.example.auth.exception.CustomException
import com.example.auth.exception.ErrorCode
import com.example.auth.jwt.JwtProvider
import com.example.auth.model.User
import com.example.auth.redis.RedisUtil
import com.example.auth.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@RequiredArgsConstructor
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var redisUtil: RedisUtil

    fun register(registerDto: RegisterRequest): RegisterResponse {
        if (userRepository.existsByName(registerDto.name)) {
            throw CustomException(ErrorCode.DUPLICATE_USER)
        }

        val user = userRepository.save(
            User(
                name = registerDto.name,
                password = passwordEncoder.encode(registerDto.password)
            )
        )

        return RegisterResponse(jwtProvider.createToken(user.name))
    }

    fun logout(token: String) {
        try {
            if (!jwtProvider.validateToken(token)) {
                throw CustomException(ErrorCode.INVALID_TOKEN)
            }
        } catch (e: ExpiredJwtException) {
            throw CustomException(ErrorCode.EXPIRED_TOKEN)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }

        val expireTime = jwtProvider.getTokenExpiration(token).toInstant().epochSecond - Instant.now().epochSecond
        redisUtil.set(token, "", expireTime)
    }

    fun validateToken(token: String) {
        try {
            if (!jwtProvider.validateToken(token)) {
                throw CustomException(ErrorCode.INVALID_TOKEN)
            }
        } catch (e: ExpiredJwtException) {
            throw CustomException(ErrorCode.EXPIRED_TOKEN)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }
    }
}