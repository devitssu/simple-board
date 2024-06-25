package com.teamsparta.simpleboard.api.domain.board.dto

import com.teamsparta.simpleboard.api.domain.board.model.PostCategory
import com.teamsparta.simpleboard.api.domain.board.model.PostStatus
import org.hibernate.validator.constraints.Length

data class UpdatePostRequest(
    @field:Length(max = 500)
    val title: String,

    @field:Length(max = 5000)
    val content: String,

    val category: PostCategory,
    val status: PostStatus,
    val tagList: List<String>,
)
