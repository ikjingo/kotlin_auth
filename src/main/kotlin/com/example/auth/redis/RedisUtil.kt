package com.example.auth.redis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


@Component
class RedisUtil {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    fun exists(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    fun set(key: String, value: Any, expireTime: Long): Boolean {
        return try {
            redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS)
            true
        } catch (e: Exception) {
            false
        }
    }
}