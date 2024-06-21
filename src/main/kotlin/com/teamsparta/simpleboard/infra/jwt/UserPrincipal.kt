package com.teamsparta.simpleboard.infra.jwt

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserPrincipal(
    val id: Long,
    val nickname: String,
    val authorities: Collection<GrantedAuthority>
) {
    constructor(id: Long, nickname: String, roles: Set<String>) : this(
        id,
        nickname,
        roles.map { SimpleGrantedAuthority("ROLE_$it") }
    )
}