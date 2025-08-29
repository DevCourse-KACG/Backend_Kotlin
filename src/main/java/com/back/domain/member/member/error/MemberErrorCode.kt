package com.back.domain.member.member.error

import com.back.global.exception.ErrorCode
import lombok.Getter

@Getter
enum class MemberErrorCode(
    override val status: Int,
    override val message: String
) : ErrorCode {
    MEMBER_NOT_FOUND(404, "사용자를 찾을 수 없습니다.");
}
