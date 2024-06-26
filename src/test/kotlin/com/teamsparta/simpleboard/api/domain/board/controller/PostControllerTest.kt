package com.teamsparta.simpleboard.api.domain.board.controller

import com.ninjasquad.springmockk.MockkBean
import com.teamsparta.simpleboard.api.domain.auth.model.Member
import com.teamsparta.simpleboard.api.domain.auth.model.MemberRole
import com.teamsparta.simpleboard.api.domain.board.dto.PostResponse
import com.teamsparta.simpleboard.api.domain.board.service.PostService
import com.teamsparta.simpleboard.api.exception.ModelNotFoundException
import com.teamsparta.simpleboard.api.exception.NoPermissionException
import com.teamsparta.simpleboard.infra.jwt.JwtAuthenticationFilter
import com.teamsparta.simpleboard.infra.jwt.JwtPlugin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@Import(JwtAuthenticationFilter::class)
@AutoConfigureMockMvc
@ExtendWith(MockKExtension::class)
class PostControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val jwtPlugin: JwtPlugin,

    @MockkBean
    private val postService: PostService
) : DescribeSpec({

    afterContainer { clearAllMocks() }

    val member = Member(
        id = 1L,
        email = "test@test.com",
        nickname = "tester",
        password = "asdfgghhjklqwe",
        role = MemberRole.MEMBER
    )

    val token = jwtPlugin.generateToken(member.id!!, member.nickname, member.role.name)

    describe("POST /post") {
        context("토큰이 없으면") {
            it("status code == 401") {
                val result = mockMvc.perform(
                    post("/api/cv1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                                {
                                  "title": "test",
                                  "content": "test",
                                  "category": "STUDY",
                                  "status": "TODO",
                                  "tagList": [
                                    "test"
                                  ]
                                }
                            """.trimIndent()
                        )
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                result.response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }

        context("제목이 500자를 초과하면") {
            it("status code == 400") {
                val title = "*".repeat(501)

                val result = mockMvc.perform(
                    post("/api/v1/post")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                                {
                                  "title": "$title",
                                  "content": "test",
                                  "category": "STUDY",
                                  "status": "TODO",
                                  "tagList": [
                                    "test"
                                  ]
                                }
                            """.trimIndent()
                        )
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                title.length shouldBeGreaterThan 500
                result.response.status shouldBe HttpStatus.BAD_REQUEST.value()
            }
        }

        context("제목이 500자 이하면") {
            it("status code == 201") {
                val title = "*".repeat(500)

                every { postService.addPost(any(), any()) } returns PostResponse(
                    id = 1,
                    title = title,
                    content = "test",
                    category = "STUDY",
                    status = "TODO",
                    tagList = listOf("test"),
                    createdBy = "test",
                    createdAt = LocalDateTime.now()
                )

                val result = mockMvc.perform(
                    post("/api/v1/post")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                                {
                                  "title": "$title",
                                  "content": "test",
                                  "category": "STUDY",
                                  "status": "TODO",
                                  "tagList": [
                                    "test"
                                  ]
                                }
                            """.trimIndent()
                        )
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                title.length shouldBeLessThanOrEqual 500
                result.response.status shouldBe HttpStatus.CREATED.value()
            }
        }

        context("내용이 5000자를 초과하면") {
            it("status code == 400") {
                val content = "*".repeat(5001)

                val result = mockMvc.perform(
                    post("/api/v1/post")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                                {
                                  "title": "test",
                                  "content": "$content",
                                  "category": "STUDY",
                                  "status": "TODO",
                                  "tagList": [
                                    "test"
                                  ]
                                }
                            """.trimIndent()
                        )
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                content.length shouldBeGreaterThan 5000
                result.response.status shouldBe HttpStatus.BAD_REQUEST.value()
            }
        }

        context("내용이 5000자 이하면") {
            it("status code == 201") {
                val content = "*".repeat(5000)

                every { postService.addPost(any(), any()) } returns PostResponse(
                    id = 1,
                    title = "test",
                    content = content,
                    category = "STUDY",
                    status = "TODO",
                    tagList = listOf("test"),
                    createdBy = "test",
                    createdAt = LocalDateTime.now()
                )

                val result = mockMvc.perform(
                    post("/api/v1/post")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                                {
                                  "title": "test",
                                  "content": "$content",
                                  "category": "STUDY",
                                  "status": "TODO",
                                  "tagList": [
                                    "test"
                                  ]
                                }
                            """.trimIndent()
                        )
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                content.length shouldBeLessThanOrEqual 5000
                result.response.status shouldBe HttpStatus.CREATED.value()
            }
        }
    }

    describe("PUT /post/{postId}") {
        context("존재하는 게시글이 아니면") {
            it("statusCode == 404") {
                every { postService.updatePost(any(), any(), any()) } throws ModelNotFoundException("Post", 1L)

                val result = mockMvc.perform(
                    put("/api/v1/post/1")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                                {
                                  "title": "test2",
                                  "content": "content2",
                                  "category": "STUDY",
                                  "status": "DONE",
                                  "tagList": [
                                    "test2"
                                  ]
                                }
                            """.trimIndent()
                        )
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                result.response.status shouldBe HttpStatus.NOT_FOUND.value()
            }
        }
        context("작성자가 아니면") {
            it("statusCode == 403") {
                every { postService.updatePost(any(), any(), any()) } throws NoPermissionException("권한이 없습니다.")

                val result = mockMvc.perform(
                    put("/api/v1/post/1")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                                {
                                  "title": "test2",
                                  "content": "content2",
                                  "category": "STUDY",
                                  "status": "DONE",
                                  "tagList": [
                                    "test2"
                                  ]
                                }
                            """.trimIndent()
                        )
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                result.response.status shouldBe HttpStatus.FORBIDDEN.value()
            }
        }

        context("작성자이면") {
            it("정상적으로 수정된다. statusCode == 200") {
                every { postService.updatePost(any(), any(), any()) } returns PostResponse(
                    id = 1,
                    title = "test",
                    content = "test",
                    category = "STUDY",
                    status = "TODO",
                    tagList = listOf("test"),
                    createdBy = "test",
                    createdAt = LocalDateTime.now()
                )

                val result = mockMvc.perform(
                    put("/api/v1/post/1")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                                {
                                  "title": "test2",
                                  "content": "content2",
                                  "category": "STUDY",
                                  "status": "DONE",
                                  "tagList": [
                                    "test2"
                                  ]
                                }
                            """.trimIndent()
                        )
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                result.response.status shouldBe HttpStatus.OK.value()
            }
        }
    }

    describe("DELETE /post/{postId}") {

        context("존재하는 게시글이 아니면") {
            it("statusCode == 404") {
                every { postService.deletePost(any(), any()) } throws ModelNotFoundException("Post", 1L)

                val result = mockMvc.perform(
                    delete("/api/v1/post/1")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                result.response.status shouldBe HttpStatus.NOT_FOUND.value()
            }
        }

        context("작성자가 아니면") {
            it("statusCode == 403") {
                every { postService.deletePost(any(), any()) } throws NoPermissionException("권한이 없습니다.")

                val result = mockMvc.perform(
                    delete("/api/v1/post/1")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                result.response.status shouldBe HttpStatus.FORBIDDEN.value()
            }
        }

        context("작성자이면") {
            it("정상적으로 삭제된다. statusCode == 204") {
                every { postService.deletePost(any(), any()) } returns Unit

                val result = mockMvc.perform(
                    delete("/api/v1/post/1")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                result.response.status shouldBe HttpStatus.NO_CONTENT.value()
            }
        }
    }
})
