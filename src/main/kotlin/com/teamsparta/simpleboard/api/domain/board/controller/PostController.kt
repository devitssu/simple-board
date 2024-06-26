package com.teamsparta.simpleboard.api.domain.board.controller

import com.teamsparta.simpleboard.api.domain.board.dto.AddPostRequest
import com.teamsparta.simpleboard.api.domain.board.dto.PostResponse
import com.teamsparta.simpleboard.api.domain.board.dto.UpdatePostRequest
import com.teamsparta.simpleboard.api.domain.board.model.PostCategory
import com.teamsparta.simpleboard.api.domain.board.model.PostStatus
import com.teamsparta.simpleboard.api.domain.board.service.PostService
import com.teamsparta.simpleboard.infra.jwt.UserPrincipal
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/post")
class PostController(
    private val postService: PostService
) {

    @PostMapping
    fun addPost(
        @RequestBody @Valid request: AddPostRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<PostResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.addPost(request, userPrincipal))
    }

    @GetMapping("/{postId}")
    fun getPost(@PathVariable postId: Long): ResponseEntity<PostResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId))
    }

    @GetMapping
    fun getPostList(
        @PageableDefault(
            size = 5,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @RequestParam(required = false) searchType: String?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) category: PostCategory?,
        @RequestParam(required = false) status: PostStatus?,
        @RequestParam(required = false) tag: String?
    ): ResponseEntity<Page<PostResponse>> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(postService.getPostList(pageable, searchType, keyword, category, status, tag))
    }

    @PutMapping("/{postId}")
    fun updatePost(
        @PathVariable postId: Long,
        @RequestBody @Valid request: UpdatePostRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<PostResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(postId, request, userPrincipal))
    }

    @DeleteMapping("/{postId}")
    fun deletePost(
        @PathVariable postId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Void> {
        postService.deletePost(postId, userPrincipal)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
