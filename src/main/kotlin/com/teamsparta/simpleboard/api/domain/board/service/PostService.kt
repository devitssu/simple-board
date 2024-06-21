package com.teamsparta.simpleboard.api.domain.board.service

import com.teamsparta.simpleboard.api.domain.board.dto.AddPostRequest
import com.teamsparta.simpleboard.api.domain.board.dto.PostResponse
import com.teamsparta.simpleboard.infra.jwt.UserPrincipal

interface PostService {
    fun addPost(request: AddPostRequest, userPrincipal: UserPrincipal): PostResponse
}
