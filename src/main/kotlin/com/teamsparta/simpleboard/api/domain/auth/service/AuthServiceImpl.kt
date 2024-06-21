package com.teamsparta.simpleboard.api.domain.auth.service

import com.teamsparta.simpleboard.api.domain.auth.dto.SignInRequest
import com.teamsparta.simpleboard.api.domain.auth.dto.SignInResponse
import com.teamsparta.simpleboard.api.domain.auth.dto.SignUpRequest
import com.teamsparta.simpleboard.api.domain.auth.repository.MemberRepository
import com.teamsparta.simpleboard.api.exception.AlreadyExistException
import com.teamsparta.simpleboard.infra.jwt.JwtPlugin
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin
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

    override fun signIn(request: SignInRequest): SignInResponse {
        memberRepository.findByNickname(request.nickname)
            ?.also {
                check(
                    it.checkPassword(
                        passwordEncoder,
                        request.password
                    )
                ) { throw IllegalArgumentException("닉네임 또는 패스워드를 확인해주세요.") }
            }
            ?.let { return SignInResponse(jwtPlugin.generateToken(it.id!!, it.nickname, it.role.name)) }
            ?: throw IllegalArgumentException("닉네임 또는 패스워드를 확인해주세요.")
    }
}