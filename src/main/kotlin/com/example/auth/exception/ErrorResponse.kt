package com.example.auth.exception

import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Data

@Data
class ErrorResponse(
    @JsonProperty("error_code")
    val errorCode: String,
    @JsonProperty("message")
    val message: String
)