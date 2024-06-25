package com.teamsparta.simpleboard.api.domain.board.dto

import com.teamsparta.simpleboard.api.domain.auth.model.Member
import com.teamsparta.simpleboard.api.domain.board.model.Post
import com.teamsparta.simpleboard.api.domain.board.model.PostCategory
import com.teamsparta.simpleboard.api.domain.board.model.PostStatus
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

data class AddPostRequest(
    @field:Length(max = 500)
    val title: String,

    @field:Length(max = 5000)
    val content: String,

    val category: PostCategory,
    val status: PostStatus,
    val tagList: List<String>,
) {
    fun toEntity(member: Member): Post {
        return Post(
            title = title,
            content = content,
            createdAt = LocalDateTime.now(),
            createdBy = member,
            category = category,
            status = status,
        )
    }
}
