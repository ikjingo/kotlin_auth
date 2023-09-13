package com.example.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.example.auth.dto.LoginRequest
import com.example.auth.dto.RegisterRequest
import com.example.auth.exception.CustomException
import com.example.auth.exception.ErrorCode
import com.example.auth.jwt.JwtProvider
import com.example.auth.jwt.UserDetailsServiceImpl
import com.example.auth.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userDetailsService: UserDetailsServiceImpl

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @MockBean
    private lateinit var jwtProvider: JwtProvider

    @MockBean
    private lateinit var userService: UserService

    // name
    private val VALID_NAME = "user"
    private val NON_EXISTENT_NAME = "non_existent_user"

    // password
    private val VALID_PASSWORD = "password"
    private val WRONG_PASSWORD = "wrongpassword"

    // token
    private val VALID_TOKEN = "validtoken"
    private val INVALID_TOKEN = "invalidToken"

    // endpoints
    private val LOGIN_ENDPOINT = "/api/v1/users/login"
    private val LOGOUT_ENDPOINT = "/api/v1/users/logout"
    private val VALIDATE_TOKEN_ENDPOINT = "/api/v1/users/validate"
    private val REGISTER_ENDPOINT = "/api/v1/users/register"

    @Test
    fun testRegister_Success() {
        // Given
        val registerRequest = RegisterRequest(VALID_NAME, VALID_PASSWORD)

        // When
        val result = performPost(REGISTER_ENDPOINT, registerRequest)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isOk())

        // Verify
        verify(userService).register(registerRequest)
    }

    @Test
    fun testRegister_Failure_UserAlreadyExists() {
        // Given
        val registerRequest = RegisterRequest(VALID_NAME, VALID_PASSWORD)
        given(userService.register(registerRequest)).willThrow(CustomException(ErrorCode.DUPLICATE_USER))

        // When
        val result = performPost(REGISTER_ENDPOINT, registerRequest)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isConflict())
    }

    @Test
    fun testLogin_Success() {
        // Given
        val userDetails = User.builder()
            .username(VALID_NAME)
            .password(passwordEncoder.encode(VALID_PASSWORD))
            .roles("USER")
            .build()
        given(userDetailsService.loadUserByUsername(VALID_NAME)).willReturn(userDetails)

        // When
        val loginRequest = LoginRequest(VALID_NAME, VALID_PASSWORD)
        val result = performPost(LOGIN_ENDPOINT, loginRequest)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.header().exists("Authorization"))
    }

    @Test
    fun testLogin_Failure_WrongPassword() {
        // Given
        val userDetails = User.builder()
            .username(VALID_NAME)
            .password(passwordEncoder.encode(VALID_PASSWORD))
            .authorities(listOf(SimpleGrantedAuthority("USER")))
            .build()
        given(userDetailsService.loadUserByUsername(VALID_NAME)).willReturn(userDetails)

        // When
        val loginRequest = LoginRequest(VALID_NAME, WRONG_PASSWORD)
        val result = performPost(LOGIN_ENDPOINT, loginRequest)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    fun testLogin_Failure_UserNotFound() {
        // Given
        given(userDetailsService.loadUserByUsername(NON_EXISTENT_NAME)).willThrow(UsernameNotFoundException("User not found"))

        // When
        val loginRequest = LoginRequest(NON_EXISTENT_NAME, VALID_PASSWORD)
        val result = performPost(LOGIN_ENDPOINT, loginRequest)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    fun testLogout_Success() {
        // Given
        val userDetails = User.builder()
            .username(VALID_NAME)
            .password(passwordEncoder.encode(VALID_PASSWORD))
            .authorities(listOf(SimpleGrantedAuthority("USER")))
            .build()
        given(userDetailsService.loadUserByUsername(VALID_NAME)).willReturn(userDetails)

        val mockExpirationDate = Date()
        given(jwtProvider.createToken(VALID_NAME)).willReturn(VALID_TOKEN)
        given(jwtProvider.validateToken(VALID_TOKEN)).willReturn(true)
        given(jwtProvider.getTokenExpiration(VALID_TOKEN)).willReturn(mockExpirationDate)
        given(jwtProvider.getUserNameFromToken(VALID_TOKEN)).willReturn(VALID_NAME)

        // When
        val result = performPost(LOGOUT_ENDPOINT, token = VALID_TOKEN)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    fun testLogout_Failure_Unauthenticated() {
        // Given

        // When
        val result = performPost(LOGOUT_ENDPOINT)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    fun testLogout_Failure_InvalidToken() {
        // Given
        given(userService.logout(INVALID_TOKEN)).willThrow(CustomException(ErrorCode.INVALID_TOKEN))

        // When
        val result = performPost(LOGOUT_ENDPOINT, token = INVALID_TOKEN)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    fun testValidateToken_Success() {
        // Given
        val userDetails = User.builder()
            .username(VALID_NAME)
            .password(passwordEncoder.encode(VALID_PASSWORD))
            .authorities(listOf(SimpleGrantedAuthority("USER")))
            .build()
        given(userDetailsService.loadUserByUsername(VALID_NAME)).willReturn(userDetails)
        given(jwtProvider.validateToken(VALID_TOKEN)).willReturn(true)
        given(jwtProvider.getUserNameFromToken(VALID_TOKEN)).willReturn(VALID_NAME)

        // When
        val result = performGet(VALIDATE_TOKEN_ENDPOINT, token = VALID_TOKEN)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    fun testValidateToken_Failure_InvalidToken() {
        // Given
        given(userService.validateToken(INVALID_TOKEN)).willThrow(CustomException(ErrorCode.INVALID_TOKEN))

        // When
        val result = performGet(VALIDATE_TOKEN_ENDPOINT, token = INVALID_TOKEN)

        // Then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    private fun performPost(endpoint: String, requestBody: Any? = null, token: String = ""): ResultActions {
        val builder = MockMvcRequestBuilders.post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)

        if (requestBody != null) {
            builder.content(objectMapper.writeValueAsString(requestBody))
        }
        if (token != "") {
            builder.header("Authorization", "Bearer $token")
        }

        return mockMvc.perform(builder)
    }

    private fun performGet(endpoint: String, token: String = ""): ResultActions {
        val builder = MockMvcRequestBuilders.get(endpoint)
            .contentType(MediaType.APPLICATION_JSON)

        if (token != "") {
            builder.header("Authorization", "Bearer $token")
        }

        return mockMvc.perform(builder)
    }
}