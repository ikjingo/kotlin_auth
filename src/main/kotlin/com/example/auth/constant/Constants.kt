package com.example.auth.constant

class Constants {
    companion object {
        //ERROR_CODE
        const val ERR_CODE_DUPLICATE_USER = "ERR_CODE_DUPLICATE_USER"
        const val ERR_CODE_INVALID_TOKEN = "ERR_CODE_INVALID_TOKEN"
        const val ERR_CODE_EXPIRED_TOKEN = "ERR_CODE_EXPIRED_TOKEN"

        //JWT
        const val DEFAULT_ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 60
    }
}