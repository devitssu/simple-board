package com.teamsparta.simpleboard.api.domain.board.dto

import org.hibernate.validator.constraints.Length

data class UpdatePostRequest(
    @field:Length(max = 500)
    val title: String,

    @field:Length(max = 5000)
    val content: String
)
