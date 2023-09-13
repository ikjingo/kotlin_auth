package com.example.auth.exception

import lombok.AllArgsConstructor
import lombok.Getter


@AllArgsConstructor
@Getter
class CustomException(val errorCode: ErrorCode) : RuntimeException()