package com.teamsparta.simpleboard.api.domain.board.repository

import com.teamsparta.simpleboard.api.domain.board.model.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long>
