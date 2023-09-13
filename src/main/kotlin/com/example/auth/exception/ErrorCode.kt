package com.example.auth.exception

import com.example.auth.constant.Constants
import lombok.AllArgsConstructor
import lombok.Getter
import org.springframework.http.HttpStatus

@AllArgsConstructor
@Getter
enum class ErrorCode(val status: Int, val errorCode: String, val message: String) {
    DUPLICATE_USER(HttpStatus.CONFLICT.value(), Constants.ERR_CODE_DUPLICATE_USER, "이미 존재하는 유저입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), Constants.ERR_CODE_INVALID_TOKEN, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), Constants.ERR_CODE_EXPIRED_TOKEN, "만료된 토큰입니다."),
}