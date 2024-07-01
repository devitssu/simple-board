package com.teamsparta.simpleboard.api.domain.board.repository

import com.teamsparta.simpleboard.api.domain.board.dto.PostRead
import com.teamsparta.simpleboard.api.domain.board.dto.SearchType
import com.teamsparta.simpleboard.api.domain.board.model.PostCategory
import com.teamsparta.simpleboard.api.domain.board.model.PostStatus
import org.springframework.data.domain.Pageable

interface PostQueryDslRepository {
    fun findByPageableAndConditions(
        pageable: Pageable,
        searchType: SearchType?,
        keyword: String?,
        category: PostCategory?,
        status: PostStatus?,
        tag: String?
    ): Pair<List<PostRead>, Long>
}