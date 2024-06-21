package com.teamsparta.simpleboard.api.domain.board.service

import com.teamsparta.simpleboard.api.domain.auth.repository.MemberRepository
import com.teamsparta.simpleboard.api.domain.board.dto.AddPostRequest
import com.teamsparta.simpleboard.api.domain.board.dto.PostResponse
import com.teamsparta.simpleboard.api.domain.board.repository.PostRepository
import com.teamsparta.simpleboard.infra.jwt.UserPrincipal
import jakarta.persistence.EntityNotFoundException
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
        val member = memberRepository.findByIdOrNull(userPrincipal.id) ?: throw EntityNotFoundException()
        return PostResponse.from(postRepository.save(request.toEntity(member)))
    }
}