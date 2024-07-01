package com.teamsparta.simpleboard.api.domain.board.repository

import com.teamsparta.simpleboard.api.domain.auth.model.Member
import com.teamsparta.simpleboard.api.domain.auth.model.MemberRole
import com.teamsparta.simpleboard.api.domain.auth.repository.MemberRepository
import com.teamsparta.simpleboard.api.domain.board.dto.SearchType
import com.teamsparta.simpleboard.api.domain.board.model.Post
import com.teamsparta.simpleboard.api.domain.board.model.PostCategory
import com.teamsparta.simpleboard.api.domain.board.model.PostStatus
import com.teamsparta.simpleboard.api.domain.board.model.PostTag
import com.teamsparta.simpleboard.api.domain.board.model.Tag
import com.teamsparta.simpleboard.infra.querydsl.QueryDslConfig
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class])
@ActiveProfiles("test")
class PostRepositoryTest @Autowired constructor(
    private val memberRepository: MemberRepository,
    private val tagRepository: TagRepository,
    private val postRepository: PostRepository
) : BehaviorSpec({

    beforeContainer {
        memberRepository.saveAllAndFlush(memberList)
        tagRepository.saveAllAndFlush(tagList)
        postList.forEachIndexed { i, post -> post.tags.addAll(postTagList[i]) }
        postRepository.saveAllAndFlush(postList)

    }

    given("findByPageableAndConditions(): ") {
        `when`("SearchType이 TITLE이고 keyword가 '3'이면") {
            then("제목에 '3'이 포함된 항목만 조회된다.") {
                val result =
                    postRepository.findByPageableAndConditions(defaultPageable, SearchType.TITLE, "3", null, null, null)

                result.first.size shouldBe postList.filter { it.title.contains("3") }.size
                result.first.forEach { it.post.title shouldContain "3" }
            }
        }
        `when`("SearchType이 CONTENT이고 keyword가 '11'이면") {
            then("내용에 11이 포함된 항목만 조회된다.") {
                val result =
                    postRepository.findByPageableAndConditions(
                        defaultPageable,
                        SearchType.CONTENT,
                        "11",
                        null,
                        null,
                        null
                    )

                result.first.size shouldBe postList.filter { it.content.contains("11") }.size
                result.first.forEach { it.post.content shouldContain "11" }
            }
        }
        `when`("SearchType이 NICKNAME이고 keyword가 'first'이면") {
            then("작성자 nickname에 'first'가 포함된 항목만 조회된다.") {
                val result =
                    postRepository.findByPageableAndConditions(
                        defaultPageable,
                        SearchType.NICKNAME,
                        "first",
                        null,
                        null,
                        null
                    )

                result.first.size shouldBe postList.filter { it.createdBy.nickname.contains("first") }.size
                result.first.forEach { it.post.createdBy.nickname shouldContain "first" }
            }
        }

        `when`("category가 STUDY이면") {
            then("category == PostCategory.STUDY인 Post만 조회된다.") {
                val result =
                    postRepository.findByPageableAndConditions(
                        defaultPageable,
                        null,
                        null,
                        PostCategory.STUDY,
                        null,
                        null
                    )

                result.first.size shouldBe postList.filter { it.category == PostCategory.STUDY }.size
                result.first.forEach { it.post.category shouldBe PostCategory.STUDY }
            }
        }

        `when`("status가 DONE이면") {
            then("status == PostStatus.DONE인 Post만 조회된다.") {
                val result =
                    postRepository.findByPageableAndConditions(
                        defaultPageable,
                        null,
                        null,
                        null,
                        PostStatus.DONE,
                        null
                    )

                result.first.size shouldBe postList.filter { it.status == PostStatus.DONE }.size
                result.first.forEach { it.post.status shouldBe PostStatus.DONE }
            }
        }
        `when`("tag가 'stu'이면") {
            then("'stu'를 포함한 태그를 가진 Post가 조회된다.") {
                val result = postRepository.findByPageableAndConditions(
                    defaultPageable,
                    null,
                    null,
                    null,
                    null,
                    "stu"
                )

                result.first.forEach { it.tags.any { tag -> tag?.name!!.contains("stu") } shouldBe true }

            }
        }
        `when`("pageable의 pageSize가 주어지면") {
            then("pageSize만큼 Post를 조회한다.") {
                val pageableWithPageSize2 = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"))
                val pageableWithPageSize5 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
                val pageableWithPageSize10 = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))

                val result1 =
                    postRepository.findByPageableAndConditions(pageableWithPageSize2, null, null, null, null, null)
                val result2 =
                    postRepository.findByPageableAndConditions(pageableWithPageSize5, null, null, null, null, null)
                val result3 =
                    postRepository.findByPageableAndConditions(pageableWithPageSize10, null, null, null, null, null)

                result1.first.size shouldBe 2
                result2.first.size shouldBe 5
                result3.first.size shouldBe 10

                result1.second shouldBeEqualComparingTo result2.second
                result2.second shouldBeEqualComparingTo result3.second
            }
        }

    }

}) {
    companion object {
        val defaultPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))

        val memberList = listOf(
            Member("test1@naver.com", "first", "test", MemberRole.MEMBER, 1L),
            Member("test2@gmail.com", "second", "test", MemberRole.MEMBER, 2L)
        )

        val tagList = listOf(
            Tag(1L, "stupid"),
            Tag(2L, "study"),
            Tag(3L, "work"),
            Tag(4L, "boxing"),
            Tag(5L, "clean"),
            Tag(6L, "life"),
            Tag(7L, "coding"),
            Tag(8L, "project"),
        )

        val postList = listOf(
            Post(
                1L,
                "test1",
                "111111",
                memberList[0],
                PostCategory.STUDY,
                status = PostStatus.TODO,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 30, 15),
                null
            ),
            Post(
                2L,
                "test2",
                "222222",
                memberList[1],
                PostCategory.WORK,
                status = PostStatus.TODO,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 30, 19),
                null
            ),
            Post(
                3L,
                "test3",
                "333333",
                memberList[1],
                PostCategory.STUDY,
                status = PostStatus.IN_PROGRESS,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 31, 15),
                null
            ),
            Post(
                4L,
                "test4",
                "444444",
                memberList[0],
                PostCategory.EXERCISE,
                status = PostStatus.DONE,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 32, 15),
                null
            ),
            Post(
                5L,
                "test5",
                "555555",
                memberList[0],
                PostCategory.LIFE,
                status = PostStatus.DONE,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 32, 55),
                null
            ),
            Post(
                6L,
                "test6",
                "666666",
                memberList[1],
                PostCategory.LIFE,
                status = PostStatus.TODO,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 33, 0),
                null
            ),
            Post(
                7L,
                "test7",
                "777777",
                memberList[1],
                PostCategory.WORK,
                status = PostStatus.IN_PROGRESS,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 33, 7),
                null
            ),
            Post(
                8L,
                "test8",
                "888888",
                memberList[1],
                PostCategory.STUDY,
                status = PostStatus.TODO,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 34, 21),
                null
            ),
            Post(
                9L,
                "test9",
                "999999",
                memberList[1],
                PostCategory.STUDY,
                status = PostStatus.TODO,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 34, 58),
                null
            ),
            Post(
                10L,
                "test10",
                "101010010101",
                memberList[0],
                PostCategory.LIFE,
                status = PostStatus.IN_PROGRESS,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 35, 2),
                null
            ),
            Post(
                11L,
                "test11",
                "1111111111",
                memberList[0],
                PostCategory.STUDY,
                status = PostStatus.DONE,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 35, 15),
                null
            ),
            Post(
                12L,
                "test12",
                "12121212121212",
                memberList[1],
                PostCategory.EXERCISE,
                status = PostStatus.TODO,
                tags = mutableSetOf(),
                createdAt = LocalDateTime.of(2024, 6, 26, 12, 37, 15),
                null
            ),
        )

        val postTagList = listOf(
            listOf(PostTag(1, postList[0], tagList[0]), PostTag(2, postList[0], tagList[1])),
            listOf(PostTag(3, postList[1], tagList[2])),
            listOf(
                PostTag(4, postList[2], tagList[0]),
                PostTag(5, postList[2], tagList[1]),
                PostTag(6, postList[2], tagList[6])
            ),
            listOf(PostTag(7, postList[3], tagList[3])),
            listOf(PostTag(8, postList[4], tagList[0])),
            listOf(PostTag(9, postList[5], tagList[4]), PostTag(10, postList[5], tagList[5])),
            listOf(PostTag(11, postList[6], tagList[0])),
            listOf(PostTag(12, postList[7], tagList[0]), PostTag(13, postList[7], tagList[1])),
            listOf(
                PostTag(14, postList[8], tagList[1]),
                PostTag(15, postList[8], tagList[2]),
                PostTag(16, postList[8], tagList[7])
            ),
            listOf(PostTag(17, postList[9], tagList[4]), PostTag(18, postList[9], tagList[5])),
            listOf(
                PostTag(19, postList[10], tagList[0]),
                PostTag(20, postList[10], tagList[1]),
                PostTag(21, postList[10], tagList[7])
            ),
            listOf(PostTag(22, postList[11], tagList[0]), PostTag(23, postList[11], tagList[3])),
        )
    }
}
