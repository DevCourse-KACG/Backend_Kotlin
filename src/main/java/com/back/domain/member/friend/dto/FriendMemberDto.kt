package com.back.domain.member.friend.dto

import com.back.domain.member.member.entity.Member
import io.swagger.v3.oas.annotations.media.Schema

@JvmRecord
data class FriendMemberDto(
    @Schema(description = "친구(회원) ID")
    val friendMemberId: Long?, // TODO: member id 낫널로 변경시 반영 예정
    @Schema(description = "친구(회원) 닉네임")
    val friendNickname: String,
    @Schema(description = "친구(회원) 자기소개")
    val friendBio: String?,
    @Schema(description = "친구(회원) 프로필 이미지 URL")
    val friendProfileImageUrl: String?
) {
    companion object {
        @JvmStatic
        fun from(friendMember: Member): FriendMemberDto {
            val memberInfo = friendMember.getMemberInfo()

            return FriendMemberDto(
                friendMemberId = friendMember.id,
                friendNickname = friendMember.nickname,
                friendBio = memberInfo?.bio,
                friendProfileImageUrl = memberInfo?.profileImageUrl
            )
        }
    }
}
