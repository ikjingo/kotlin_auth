package com.example.auth.controller

import com.example.auth.dto.RegisterRequest
import com.example.auth.exception.CustomException
import com.example.auth.exception.ErrorCode
import com.example.auth.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {
    @PostMapping("/register")
    fun register(@RequestBody registerDto: RegisterRequest): ResponseEntity<*> {
        return ResponseEntity(userService.register(registerDto), HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") auth: String?): ResponseEntity<*> {
        if (auth == null) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }
        val token: String = auth.removePrefix("Bearer ").trim()
        return ResponseEntity(userService.logout(token), HttpStatus.NO_CONTENT)
    }

    @GetMapping("/validate")
    fun validate(@RequestHeader("Authorization") auth: String?): ResponseEntity<*> {
        if (auth == null) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }
        val token: String = auth.removePrefix("Bearer ").trim()
        return ResponseEntity(userService.validateToken(token), HttpStatus.NO_CONTENT)
    }
}