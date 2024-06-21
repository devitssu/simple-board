package com.teamsparta.simpleboard.api.domain.auth.controller

import com.teamsparta.simpleboard.api.domain.auth.dto.SignInRequest
import com.teamsparta.simpleboard.api.domain.auth.dto.SignInResponse
import com.teamsparta.simpleboard.api.domain.auth.dto.SignUpRequest
import com.teamsparta.simpleboard.api.domain.auth.service.AuthService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @GetMapping("/check-nickname")
    fun checkNickname(
        @RequestParam
        @Length(min = 3, message = "최소 3자 이상이어야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "알파벳 대소문자(a~z, A~Z), 숫자(0~9)만 입력 가능합니다.")
        nickname: String
    ): ResponseEntity<Unit> {
        authService.checkNickname(nickname)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @GetMapping("/check-email")
    fun checkEmail(@RequestParam @Email email: String): ResponseEntity<Unit> {
        authService.checkEmail(email)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @PostMapping("/sign-up")
    fun signUp(@RequestBody @Valid request: SignUpRequest): ResponseEntity<Unit> {
        authService.signUp(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/sign-in")
    fun signIn(@RequestBody request: SignInRequest): ResponseEntity<SignInResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.signIn(request))
    }
}