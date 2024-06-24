package com.teamsparta.simpleboard.api.domain.board.service

import com.teamsparta.simpleboard.api.domain.auth.repository.MemberRepository
import com.teamsparta.simpleboard.api.domain.board.dto.AddPostRequest
import com.teamsparta.simpleboard.api.domain.board.dto.PostResponse
import com.teamsparta.simpleboard.api.domain.board.dto.UpdatePostRequest
import com.teamsparta.simpleboard.api.domain.board.repository.PostRepository
import com.teamsparta.simpleboard.api.exception.ModelNotFoundException
import com.teamsparta.simpleboard.infra.jwt.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val memberRepository: MemberRepository
) : PostService {

    @Transactional
    override fun addPost(request: AddPostRequest, userPrincipal: UserPrincipal): PostResponse {
        val member = memberRepository.findByIdOrNull(userPrincipal.id) ?: throw ModelNotFoundException(
            "Member",
            userPrincipal.id
        )
        return PostResponse.from(postRepository.save(request.toEntity(member)))
    }

    override fun getPost(postId: Long): PostResponse {
        return postRepository.findByIdOrNull(postId)
            ?.let { PostResponse.from(it) }
            ?: throw ModelNotFoundException("Post", postId)
    }

    @Transactional
    override fun updatePost(postId: Long, request: UpdatePostRequest, userPrincipal: UserPrincipal): PostResponse {
        return postRepository.findByIdOrNull(postId)
            ?.also { it.update(userPrincipal.id, request.title, request.content) }
            ?.let { PostResponse.from(it) }
            ?: throw ModelNotFoundException("Post", postId)
    }
}