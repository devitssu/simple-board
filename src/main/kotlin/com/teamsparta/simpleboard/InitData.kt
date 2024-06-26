package com.teamsparta.simpleboard

import com.teamsparta.simpleboard.api.domain.auth.model.Member
import com.teamsparta.simpleboard.api.domain.auth.model.MemberRole
import com.teamsparta.simpleboard.api.domain.auth.repository.MemberRepository
import com.teamsparta.simpleboard.api.domain.board.dto.AddPostRequest
import com.teamsparta.simpleboard.api.domain.board.model.PostCategory
import com.teamsparta.simpleboard.api.domain.board.model.PostStatus
import com.teamsparta.simpleboard.api.domain.board.service.PostService
import com.teamsparta.simpleboard.infra.jwt.UserPrincipal
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class InitData(
    private val memberRepository: MemberRepository,
    private val postService: PostService,
    private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val member1 = memberRepository.save(
            Member(
                email = "test@test.com",
                nickname = "test",
                password = passwordEncoder.encode("string"),
                role = MemberRole.MEMBER,
            )
        )

        val member2 = memberRepository.save(
            Member(
                email = "test2@test.com",
                nickname = "second",
                password = passwordEncoder.encode("string"),
                role = MemberRole.MEMBER,
            )
        )

        postService.addPost(
            AddPostRequest(
                title = "test1",
                content = "test",
                category = PostCategory.STUDY,
                status = PostStatus.TODO,
                tagList = listOf("test", "study"),
            ),
            UserPrincipal(member1.id!!, nickname = member1.nickname, setOf("MEMBER"))
        )
        postService.addPost(
            AddPostRequest(
                title = "test2",
                content = "2222222",
                category = PostCategory.STUDY,
                status = PostStatus.DONE,
                tagList = listOf("test", "study", "second"),
            ),
            UserPrincipal(member1.id!!, nickname = member1.nickname, setOf("MEMBER"))
        )
        postService.addPost(
            AddPostRequest(
                title = "test3",
                content = "333333",
                category = PostCategory.WORK,
                status = PostStatus.TODO,
                tagList = listOf("test", "work"),
            ),
            UserPrincipal(member1.id!!, nickname = member1.nickname, setOf("MEMBER"))
        )
        postService.addPost(
            AddPostRequest(
                title = "test4",
                content = "4444444",
                category = PostCategory.LIFE,
                status = PostStatus.TODO,
                tagList = listOf("test", "life"),
            ),
            UserPrincipal(member2.id!!, nickname = member2.nickname, setOf("MEMBER"))
        )
        postService.addPost(
            AddPostRequest(
                title = "test5",
                content = "55555555",
                category = PostCategory.STUDY,
                status = PostStatus.IN_PROGRESS,
                tagList = listOf("test", "study"),
            ),
            UserPrincipal(member1.id!!, nickname = member1.nickname, setOf("MEMBER"))
        )
        postService.addPost(
            AddPostRequest(
                title = "test6",
                content = "66666666",
                category = PostCategory.EXERCISE,
                status = PostStatus.DONE,
                tagList = listOf("boxing"),
            ),
            UserPrincipal(member2.id!!, nickname = member2.nickname, setOf("MEMBER"))
        )
        postService.addPost(
            AddPostRequest(
                title = "test7",
                content = "777777777",
                category = PostCategory.STUDY,
                status = PostStatus.TODO,
                tagList = listOf(),
            ),
            UserPrincipal(member1.id!!, nickname = member1.nickname, setOf("MEMBER"))
        )
        postService.addPost(
            AddPostRequest(
                title = "test8",
                content = "8888888888",
                category = PostCategory.STUDY,
                status = PostStatus.TODO,
                tagList = listOf("TIL", "writing"),
            ),
            UserPrincipal(member2.id!!, nickname = member2.nickname, setOf("MEMBER"))
        )
        postService.addPost(
            AddPostRequest(
                title = "test9",
                content = "999999999",
                category = PostCategory.EXERCISE,
                status = PostStatus.IN_PROGRESS,
                tagList = listOf("boxing"),
            ),
            UserPrincipal(member2.id!!, nickname = member2.nickname, setOf("MEMBER"))
        )
        postService.addPost(
            AddPostRequest(
                title = "test10",
                content = "1010101010101010",
                category = PostCategory.LIFE,
                status = PostStatus.TODO,
                tagList = listOf("clean"),
            ),
            UserPrincipal(member1.id!!, nickname = member1.nickname, setOf("MEMBER"))
        )
    }
}