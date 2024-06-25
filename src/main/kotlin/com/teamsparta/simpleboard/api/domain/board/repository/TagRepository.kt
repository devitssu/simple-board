package com.teamsparta.simpleboard.api.domain.board.repository

import com.teamsparta.simpleboard.api.domain.board.model.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {
    fun findByName(name: String): Tag?
}
