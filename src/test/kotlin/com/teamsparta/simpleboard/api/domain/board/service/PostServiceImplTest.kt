package com.teamsparta.simpleboard.api.domain.board.service

import com.teamsparta.simpleboard.api.domain.auth.model.Member
import com.teamsparta.simpleboard.api.domain.auth.model.MemberRole
import com.teamsparta.simpleboard.api.domain.auth.repository.MemberRepository
import com.teamsparta.simpleboard.api.domain.board.dto.AddPostRequest
import com.teamsparta.simpleboard.api.domain.board.dto.PostResponse
import com.teamsparta.simpleboard.api.domain.board.dto.UpdatePostRequest
import com.teamsparta.simpleboard.api.domain.board.model.Post
import com.teamsparta.simpleboard.api.domain.board.model.PostCategory
import com.teamsparta.simpleboard.api.domain.board.model.PostStatus
import com.teamsparta.simpleboard.api.domain.board.repository.PostRepository
import com.teamsparta.simpleboard.api.domain.board.repository.TagRepository
import com.teamsparta.simpleboard.api.exception.ModelNotFoundException
import com.teamsparta.simpleboard.api.exception.NoPermissionException
import com.teamsparta.simpleboard.infra.jwt.UserPrincipal
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class PostServiceImplTest : BehaviorSpec({
    val postRepository: PostRepository = mockk(relaxed = true)
    val memberRepository: MemberRepository = mockk()
    val tagRepository: TagRepository = mockk()

    val postService = PostServiceImpl(postRepository, memberRepository, tagRepository)

    afterContainer { clearAllMocks() }

    val userPrinciple = UserPrincipal(
        id = 1L,
        nickname = "test",
        roles = setOf("MEMBER")
    )

    val writer = Member(
        id = 1L,
        email = "test@test.com",
        nickname = "tester",
        password = "asdfgghhjklqwe",
        role = MemberRole.MEMBER
    )

    val postCreatedByWriter = Post(
        id = 1L,
        title = "test",
        content = "test",
        category = PostCategory.STUDY,
        status = PostStatus.TODO,
        createdBy = writer,
        createdAt = LocalDateTime.of(2024, 6, 26, 12, 30, 15)
    )

    given("존재하지 않는 Member가") {
        `when`("addPost()를 실행하면") {
            then("ModelNotFoundException이 발생한다.") {
                every { memberRepository.findByIdOrNull(any()) } returns null

                shouldThrow<ModelNotFoundException> {
                    postService.addPost(
                        AddPostRequest(
                            title = "test",
                            content = "test",
                            category = PostCategory.STUDY,
                            status = PostStatus.TODO,
                            tagList = listOf("test")
                        ),
                        userPrinciple
                    )
                }
            }
        }
    }

    given("존재하는 Member가") {
        `when`("addPost()를 실행하면") {
            then("새로운 Post가 생성되고 PostResponse를 반환한다.") {
                val request = AddPostRequest(
                    title = "test",
                    content = "test",
                    category = PostCategory.STUDY,
                    status = PostStatus.TODO,
                    tagList = listOf()
                )

                val savedPost = Post(
                    id = 1L,
                    title = "test",
                    content = "test",
                    category = PostCategory.STUDY,
                    status = PostStatus.TODO,
                    createdBy = writer,
                    createdAt = LocalDateTime.now()
                )

                every { memberRepository.findByIdOrNull(any()) } returns writer
                every { postRepository.save(any()) } returns savedPost
                postService.addPost(request, userPrinciple) shouldBe PostResponse.from(savedPost)
            }
        }
    }

    given("존재하지 않는 Post의 id로") {
        `when`("updatePost()를 실행하면") {
            then("ModelNotFoundException이 발생한다.") {
                val request = UpdatePostRequest(
                    title = "test",
                    content = "test",
                    category = PostCategory.STUDY,
                    status = PostStatus.TODO,
                    tagList = listOf()
                )

                every { postRepository.findByIdOrNull(1L) } returns null

                shouldThrow<ModelNotFoundException> {
                    postService.updatePost(1L, request, userPrinciple)
                }
            }
        }
        `when`("deletePost()를 실행하면") {
            then("ModelNotFoundException이 발생한다.") {
                every { postRepository.findByIdOrNull(1L) } returns null

                shouldThrow<ModelNotFoundException> {
                    postService.deletePost(1L, userPrinciple)
                }
            }
        }
    }

    given("Post의 작성자가 아닌 Member가") {
        `when`("updatePost()를 실행하면") {
            then("NoPermissionException이 발생한다.") {
                every { postRepository.findByIdOrNull(1L) } returns postCreatedByWriter

                val request = UpdatePostRequest(
                    title = "test",
                    content = "test",
                    category = PostCategory.STUDY,
                    status = PostStatus.TODO,
                    tagList = listOf()
                )

                val otherUserPrincipal = UserPrincipal(
                    id = 2L,
                    nickname = "other",
                    roles = setOf("MEMBER")
                )
                shouldThrow<NoPermissionException> {
                    postService.updatePost(1L, request, otherUserPrincipal)
                }
            }
        }

        `when`("deletePost()를 실행하면") {
            then("NoPermissionException이 발생한다.") {
                every { postRepository.findByIdOrNull(1L) } returns postCreatedByWriter

                val otherUserPrincipal = UserPrincipal(
                    id = 2L,
                    nickname = "other",
                    roles = setOf("MEMBER")
                )

                shouldThrow<NoPermissionException> {
                    postService.deletePost(1L, otherUserPrincipal)
                }
            }
        }
    }

    given("Post의 작성자인 Member가") {
        `when`("updatePost()를 실행하면") {
            then("해당 Post를 수정하고 PostResponse를 반환한다.") {
                every { postRepository.findByIdOrNull(1L) } returns postCreatedByWriter

                val request = UpdatePostRequest(
                    title = "updated",
                    content = "updated",
                    category = PostCategory.LIFE,
                    status = PostStatus.DONE,
                    tagList = listOf()
                )

                postService.updatePost(1L, request, userPrinciple) shouldBe PostResponse.from(
                    Post(
                        id = 1L,
                        title = "updated",
                        content = "updated",
                        category = PostCategory.LIFE,
                        status = PostStatus.DONE,
                        createdBy = writer,
                        createdAt = LocalDateTime.of(2024, 6, 26, 12, 30, 15)
                    )
                )
            }
        }

        `when`("deletePost()를 실행하면") {
            then("해당 Post를 삭제하고 아무것도 반환하지 않는다.") {
                every { postRepository.findByIdOrNull(1L) } returns postCreatedByWriter

                postService.deletePost(1L, userPrinciple)

                verify { postRepository.delete(postCreatedByWriter) }
            }
        }
    }

})
