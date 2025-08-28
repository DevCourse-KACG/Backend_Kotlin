package com.back.global.security

import com.back.global.enums.MemberType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

// member에 맞춰서 수정 예정
class SecurityUser(
    val id: Long,
    val nickname: String,
    val tag: String,
    val memberType: MemberType,
    password: String,
    authorities: MutableCollection<out GrantedAuthority>
) : User(nickname, password, authorities)
