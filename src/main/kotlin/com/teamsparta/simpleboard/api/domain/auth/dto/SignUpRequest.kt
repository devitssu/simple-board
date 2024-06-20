package com.teamsparta.simpleboard.api.domain.auth.dto

import com.teamsparta.simpleboard.api.domain.auth.model.Member
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
import org.springframework.security.crypto.password.PasswordEncoder

data class SignUpRequest(

    @field:NotBlank(message = "닉네임은 필수입니다.")
    @field:Length(min = 3, message = "최소 3자 이상이어야 합니다.")
    @field:Pattern(regexp = "^[a-zA-Z0-9]*$", message = "알파벳 대소문자(a~z, A~Z), 숫자(0~9)만 입력 가능합니다.")
    val nickname: String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Length(min = 3, message = "최소 4자 이상이어야 합니다.")
    @field:Pattern(regexp = "^[a-zA-Z0-9]*$", message = "알파벳 대소문자(a~z, A~Z), 숫자(0~9)만 입력 가능합니다.")
    val password: String,

    @field:NotBlank(message = "비밀번호 확인은 필수입니다.")
    val passwordCheck: String,
) {
    fun to(passwordEncoder: PasswordEncoder): Member {
        validatePassword()

        return Member(
            email = this.email,
            nickname = this.nickname,
            password = passwordEncoder.encode(this.password)
        )
    }

    private fun validatePassword() {
        if (this.password.contains(this.nickname)) throw IllegalArgumentException("비밀번호는 닉네임을 포함할 수 없습니다. ")
        if (this.password != this.passwordCheck) throw IllegalArgumentException("비밀번호 확인이 비밀번호와 다릅니다.")
    }
}
