package com.teamsparta.simpleboard.api.domain.board.dto

import com.teamsparta.simpleboard.api.domain.board.model.Post
import java.time.LocalDateTime

data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdBy: String,
    val category: String,
    val status: String,
    val tagList: List<String>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(post: Post): PostResponse {
            return PostResponse(
                id = post.id!!,
                title = post.title,
                content = post.content,
                createdAt = post.createdAt,
                createdBy = post.createdBy.nickname,
                category = post.category.name,
                status = post.status.name,
                tagList = post.tags.map { it.tag.name },
            )
        }
    }
}
