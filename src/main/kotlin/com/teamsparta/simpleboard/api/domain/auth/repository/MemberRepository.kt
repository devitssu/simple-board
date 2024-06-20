package com.teamsparta.simpleboard.api.domain.auth.repository

import com.teamsparta.simpleboard.api.domain.auth.model.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
}
