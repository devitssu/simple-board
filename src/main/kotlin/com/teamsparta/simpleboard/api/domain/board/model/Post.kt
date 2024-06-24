package com.teamsparta.simpleboard.api.domain.board.model

import com.teamsparta.simpleboard.api.domain.auth.model.Member
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var title: String,
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val createdBy: Member,

    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime? = null
) {
    fun update(id: Long, title: String, content: String) {
        this.title = title
        this.content = content
        this.updatedAt = LocalDateTime.now()
    }

    fun checkPermission(id: Long): Boolean {
        return createdBy.id == id
    }
}
