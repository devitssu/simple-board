package com.teamsparta.simpleboard.infra.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.Date

@Component
class JwtPlugin(
    @Value("\${auth.jwt.issuer}") private val issuer: String,
    @Value("\${auth.jwt.secret}") private val secret: String,
    @Value("\${auth.jwt.expirationHour}") private val expirationHour: Long,
) {

    fun generateToken(memberId: Long, nickname: String, role: String): String {
        val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
        val now = Instant.now()

        val claims = Jwts.claims()
            .add(mapOf("nickname" to nickname, "role" to role))
            .build()

        return Jwts.builder()
            .subject(memberId.toString())
            .claims(claims)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(Duration.ofHours(expirationHour))))
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Result<Jws<Claims>> {
        return kotlin.runCatching {
            val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
        }
    }
}