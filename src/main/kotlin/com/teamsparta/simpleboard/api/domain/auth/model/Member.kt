package com.teamsparta.simpleboard.api.domain.auth.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.security.crypto.password.PasswordEncoder

@Entity
class Member(
    val email: String,
    val nickname: String,
    val password: String,

    @Enumerated(EnumType.STRING)
    val role: MemberRole = MemberRole.MEMBER,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
    fun checkPassword(passwordEncoder: PasswordEncoder, requestPW: String): Boolean {
        return passwordEncoder.matches(requestPW, this.password)
    }
}