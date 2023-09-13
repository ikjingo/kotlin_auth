package com.example.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.example.auth.constant.Constants
import jakarta.validation.constraints.NotNull
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor

@Data
@Builder
data class RegisterResponse(
    @NotNull
    @JsonProperty("access_token")
    val accessToken: String,
    @NotNull
    @JsonProperty("expiration")
    val expiration: Long = Constants.DEFAULT_ACCESS_TOKEN_EXPIRED_TIME
)