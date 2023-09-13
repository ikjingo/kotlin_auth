package com.example.auth.repository

import com.example.auth.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun existsByName(name: String): Boolean

    fun findUserByName(name: String): User
}