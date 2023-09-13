package com.example.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor

@Data
@Builder
data class LoginRequest(
    @NotNull
    @JsonProperty("name")
    val name: String,
    @NotNull
    @JsonProperty("password")
    val password: String
)