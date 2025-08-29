package com.back.domain.member.friend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

@JvmRecord
data class FriendAddReqBody(
    @Schema(description = "친구 요청 대상의 이메일")
    @field:NotNull
    @field:Email
    val friend_email: String
)
