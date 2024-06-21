package com.teamsparta.simpleboard.api.domain.auth.dto

data class SignInRequest(
    val nickname: String,
    val password: String
)
