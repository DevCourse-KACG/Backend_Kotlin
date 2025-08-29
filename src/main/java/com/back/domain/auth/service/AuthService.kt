package com.back.domain.auth.service

import com.back.domain.member.member.entity.Member
import com.back.global.config.jwt.JwtProperties
import com.back.global.enums.MemberType
import com.back.standard.util.Ut
import org.springframework.stereotype.Service

@Service
class AuthService(private val jwtProperties: JwtProperties) {

    fun generateAccessToken(member: Member): String {
        // 1. 회원, 비회원 공통 검증
        requireNotNull(member) { "Member 정보가 없습니다." }

        val id = member.id
        val nickname = member.nickname
        val tag = member.tag
        val memberType = member.memberType

        // 2. 회원 검증
        val email = member.getMemberInfo()?.email ?: ""

        return Ut.jwt.toString(
            jwtProperties.jwt.secretKey,
            jwtProperties.accessToken.expirationSeconds?.toInt() ?: 3600,
            mapOf(
                "id" to id,
                "email" to email,
                "nickname" to nickname,
                "tag" to tag,
                "memberType" to memberType.toString()
            )
        )
    }

    fun payload(accessToken: String): Map<String, Any>? {
        val parsedPayload = Ut.jwt.payload(jwtProperties.jwt.secretKey, accessToken) ?: return null
        val email = parsedPayload["email"] as? String ?: ""
        return mapOf("email" to email)
    }
}
