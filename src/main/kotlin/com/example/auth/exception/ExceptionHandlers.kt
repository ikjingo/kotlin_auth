package com.example.auth.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ExceptionHandlers {
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<*> {
        return ResponseEntity<Any>(
            ErrorResponse(e.errorCode.errorCode, e.errorCode.message),
            HttpStatus.valueOf(e.errorCode.status)
        )
    }
}