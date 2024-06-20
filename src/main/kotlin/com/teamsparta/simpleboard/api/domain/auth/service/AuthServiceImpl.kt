package com.teamsparta.simpleboard.api.domain.auth.service

import com.teamsparta.simpleboard.api.domain.auth.dto.SignUpRequest
import com.teamsparta.simpleboard.api.domain.auth.repository.MemberRepository
import com.teamsparta.simpleboard.api.exception.AlreadyExistException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) : AuthService {
    override fun checkNickname(nickname: String) {
        if (memberRepository.existsByNickname(nickname)) throw AlreadyExistException("닉네임")
    }

    override fun checkEmail(email: String) {
        if (memberRepository.existsByEmail(email)) throw AlreadyExistException("이메일")
    }

    @Transactional
    override fun signUp(request: SignUpRequest) {
        checkNickname(request.nickname)
        request.to(passwordEncoder)
            .let { memberRepository.save(it) }
    }
}