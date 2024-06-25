package com.teamsparta.simpleboard.api.domain.board.service

import com.teamsparta.simpleboard.api.domain.auth.repository.MemberRepository
import com.teamsparta.simpleboard.api.domain.board.dto.AddPostRequest
import com.teamsparta.simpleboard.api.domain.board.dto.PostResponse
import com.teamsparta.simpleboard.api.domain.board.dto.UpdatePostRequest
import com.teamsparta.simpleboard.api.domain.board.model.Post
import com.teamsparta.simpleboard.api.domain.board.model.PostTag
import com.teamsparta.simpleboard.api.domain.board.model.Tag
import com.teamsparta.simpleboard.api.domain.board.repository.PostRepository
import com.teamsparta.simpleboard.api.domain.board.repository.TagRepository
import com.teamsparta.simpleboard.api.exception.ModelNotFoundException
import com.teamsparta.simpleboard.api.exception.NoPermissionException
import com.teamsparta.simpleboard.infra.jwt.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val memberRepository: MemberRepository,
    private val tagRepository: TagRepository,
) : PostService {

    @Transactional
    override fun addPost(request: AddPostRequest, userPrincipal: UserPrincipal): PostResponse {
        val member = memberRepository.findByIdOrNull(userPrincipal.id) ?: throw ModelNotFoundException(
            "Member",
            userPrincipal.id
        )
        return postRepository.save(request.toEntity(member))
            .also { it.addTags(request.tagList) }
            .let { PostResponse.from(it) }
    }

    override fun getPost(postId: Long): PostResponse {
        return postRepository.findByIdOrNull(postId)
            ?.let { PostResponse.from(it) }
            ?: throw ModelNotFoundException("Post", postId)
    }

    @Transactional
    override fun updatePost(postId: Long, request: UpdatePostRequest, userPrincipal: UserPrincipal): PostResponse {
        return postRepository.findByIdOrNull(postId)
            ?.also { check(it.checkPermission(userPrincipal.id)) { throw NoPermissionException("권한이 없습니다.") } }
            ?.also { it.update(userPrincipal.id, request.title, request.content, request.status, request.category) }
            ?.also { it.updateTags(request.tagList) }
            ?.let { PostResponse.from(it) }
            ?: throw ModelNotFoundException("Post", postId)
    }

    @Transactional
    override fun deletePost(postId: Long, userPrincipal: UserPrincipal) {
        postRepository.findByIdOrNull(postId)
            ?.also { check(it.checkPermission(userPrincipal.id)) { throw NoPermissionException("권한이 없습니다.") } }
            ?.let { postRepository.delete(it) }
            ?: throw ModelNotFoundException("Post", postId)
    }

    fun Post.addTags(tagList: List<String>) {
        if (tagList.isEmpty()) return
        tagList.map { tagRepository.findByName(it) ?: tagRepository.save(Tag(name = it)) }
            .forEach { this.tags.add(PostTag(post = this, tag = it)) }
    }

    fun Post.updateTags(tagList: List<String>) {
        val origin = this.tags
        if (tagList.isEmpty()) origin.clear()

        //tagList에만 있는 태그 추가
        tagList.map { tagRepository.findByName(it) ?: tagRepository.save(Tag(name = it)) }
            .filter { !origin.map { postTag -> postTag.tag }.contains(it) }
            .map { PostTag(post = this, tag = it) }
            .also { origin.addAll(it) }

        //post.tags에만 있는 항목 삭제
        origin.removeIf { !tagList.contains(it.tag.name) }
    }
}
