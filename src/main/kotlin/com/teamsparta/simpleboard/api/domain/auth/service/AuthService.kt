package com.teamsparta.simpleboard.api.domain.auth.service

import com.teamsparta.simpleboard.api.domain.auth.dto.SignUpRequest

interface AuthService {
    fun checkNickname(nickname: String) // 닉네임 중복 확인
    fun checkEmail(email: String) // 이메일 중복 확인
    fun signUp(request: SignUpRequest) //회원 가입
}
