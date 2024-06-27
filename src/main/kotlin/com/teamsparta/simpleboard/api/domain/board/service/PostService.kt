package com.teamsparta.simpleboard.api.domain.board.service

import com.teamsparta.simpleboard.api.domain.board.dto.AddPostRequest
import com.teamsparta.simpleboard.api.domain.board.dto.PostResponse
import com.teamsparta.simpleboard.api.domain.board.dto.SearchType
import com.teamsparta.simpleboard.api.domain.board.dto.UpdatePostRequest
import com.teamsparta.simpleboard.api.domain.board.model.PostCategory
import com.teamsparta.simpleboard.api.domain.board.model.PostStatus
import com.teamsparta.simpleboard.infra.jwt.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostService {
    fun addPost(request: AddPostRequest, userPrincipal: UserPrincipal): PostResponse
    fun getPost(postId: Long): PostResponse
    fun updatePost(postId: Long, request: UpdatePostRequest, userPrincipal: UserPrincipal): PostResponse
    fun deletePost(postId: Long, userPrincipal: UserPrincipal)
    fun getPostList(
        pageable: Pageable,
        searchType: SearchType?,
        keyword: String?,
        category: PostCategory?,
        status: PostStatus?,
        tag: String?
    ): Page<PostResponse>
}
