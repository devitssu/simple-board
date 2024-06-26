package com.teamsparta.simpleboard.api.domain.board.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import com.teamsparta.simpleboard.api.domain.auth.model.QMember
import com.teamsparta.simpleboard.api.domain.board.model.Post
import com.teamsparta.simpleboard.api.domain.board.model.PostCategory
import com.teamsparta.simpleboard.api.domain.board.model.PostStatus
import com.teamsparta.simpleboard.api.domain.board.model.QPost
import com.teamsparta.simpleboard.api.domain.board.model.QPostTag
import com.teamsparta.simpleboard.api.domain.board.model.QTag.tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.util.StringUtils

class PostQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : PostQueryDslRepository {

    private val qPost = QPost.post
    private val qPostTag = QPostTag.postTag
    private val qTag = tag
    private val qMember = QMember.member

    override fun findByPageableAndConditions(
        pageable: Pageable,
        searchType: String?,
        keyword: String?,
        category: PostCategory?,
        status: PostStatus?,
        tag: String?
    ): Page<Post> {
        val totalCount = queryFactory
            .select(qPost.countDistinct())
            .from(qPost)
            .join(qPost.createdBy, qMember)
            .leftJoin(qPost.tags, qPostTag)
            .leftJoin(qPostTag.tag, qTag)
            .where(allConditions(searchType, keyword, category, status, tag))
            .fetchOne() ?: 0L

        val paginatedPostId = queryFactory
            .select(qPost).distinct()
            .from(qPost)
            .join(qPost.createdBy, qMember)
            .leftJoin(qPost.tags, qPostTag)
            .leftJoin(qPostTag.tag, qTag)
            .where(allConditions(searchType, keyword, category, status, tag))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable, qPost))
            .fetch()

        val contents = queryFactory.selectFrom(qPost)
            .join(qPost.createdBy, qMember).fetchJoin()
            .leftJoin(qPost.tags, qPostTag).fetchJoin()
            .leftJoin(qPostTag.tag, qTag).fetchJoin()
            .where(qPost.id.`in`(paginatedPostId.map { it.id }))
            .orderBy(*getOrderSpecifier(pageable, qPost))
            .fetch()

        return PageImpl(contents, pageable, totalCount)
    }

    private fun allConditions(
        searchType: String?,
        keyword: String?,
        category: PostCategory?,
        status: PostStatus?,
        tag: String?
    ): BooleanBuilder {
        return BooleanBuilder()
            .and(searchByKeyword(searchType, keyword))
            .and(eqCategory(category))
            .and(containsTags(tag))
            .and(eqStatus(status))
    }

    private fun searchByKeyword(searchType: String?, keyword: String?): BooleanExpression? {
        return when (searchType) {
            "title" -> containsTitle(keyword)
            "nickname" -> containsNickname(keyword)
            "content" -> containsContent(keyword)
            else -> null
        }
    }

    private fun containsTitle(title: String?): BooleanExpression? {
        return if (StringUtils.hasText(title)) qPost.title.containsIgnoreCase(title) else null
    }

    private fun containsNickname(nickname: String?): BooleanExpression? {
        return if (StringUtils.hasText(nickname)) qPost.createdBy.nickname.containsIgnoreCase(nickname) else null
    }

    private fun containsContent(content: String?): BooleanExpression? {
        return if (StringUtils.hasText(content)) qPost.content.containsIgnoreCase(content) else null
    }

    private fun eqCategory(category: PostCategory?): BooleanExpression? {
        return if (category != null) qPost.category.eq(category) else null
    }

    private fun eqStatus(status: PostStatus?): BooleanExpression? {
        return if (status != null) qPost.status.eq(status) else null
    }

    private fun containsTags(tag: String?): BooleanExpression? {
        return if (!tag.isNullOrEmpty()) qTag.name.containsIgnoreCase(tag) else null
    }

    private fun getOrderSpecifier(pageable: Pageable, path: EntityPathBase<*>): Array<OrderSpecifier<*>> {

        val pathBuilder = PathBuilder(path.type, path.metadata)

        return pageable.sort.toList().map { order ->
            OrderSpecifier(
                if (order.isAscending) Order.ASC else Order.DESC,
                pathBuilder.get(order.property) as Expression<Comparable<*>>
            )
        }.toTypedArray()
    }
}