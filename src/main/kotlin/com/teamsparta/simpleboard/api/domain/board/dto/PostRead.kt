package com.teamsparta.simpleboard.api.domain.board.dto

import com.teamsparta.simpleboard.api.domain.board.model.Post
import com.teamsparta.simpleboard.api.domain.board.model.Tag

data class PostRead(
    val post: Post,
    val tags: List<Tag?> = mutableListOf()
) {
    fun toResponse() = PostResponse(
        id = post.id!!,
        title = post.title,
        content = post.content,
        createdBy = post.createdBy.nickname,
        category = post.category.name,
        status = post.status.name,
        createdAt = post.createdAt,
        tagList = tags.map { it?.name }
    )
}
