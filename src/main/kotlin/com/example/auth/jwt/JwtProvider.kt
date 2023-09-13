package com.example.auth.jwt

import com.example.auth.constant.Constants.Companion.DEFAULT_ACCESS_TOKEN_EXPIRED_TIME
import com.example.auth.redis.RedisUtil
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec


@Component
class JwtProvider(@Value("\${jwt.secret}") private val secretKey: String) {
    @Autowired
    private lateinit var redisUtil: RedisUtil
    private fun getSecretKey(): Key = SecretKeySpec(Decoders.BASE64.decode(secretKey), SignatureAlgorithm.HS256.jcaName)

    fun createToken(name: String, expiredTime: Long = DEFAULT_ACCESS_TOKEN_EXPIRED_TIME): String {
        val claims = Jwts.claims().setSubject(name)
        claims["user_id"] = name

        val date = Date()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(date)
            .setExpiration(Date(date.time + expiredTime))
            .signWith(getSecretKey(), SignatureAlgorithm.HS512)
            .compact()
    }

    @Throws(Exception::class)
    fun validateToken(token: String): Boolean {
        if (redisUtil.exists(token)) {
            return false
        }

        val claims: Jws<Claims> = Jwts.parserBuilder()
            .setSigningKey(getSecretKey())
            .build()
            .parseClaimsJws(token)

        return !claims.body.expiration.before(Date())
    }

    fun getTokenExpiration(token: String): Date {
        val claims: Jws<Claims> = Jwts.parserBuilder()
            .setSigningKey(getSecretKey())
            .build()
            .parseClaimsJws(token)
        return claims.body.expiration
    }

    fun getUserNameFromToken(token: String): String {
        val claims: Jws<Claims> = Jwts.parserBuilder()
            .setSigningKey(getSecretKey())
            .build()
            .parseClaimsJws(token)
        return claims.body.subject
    }
}