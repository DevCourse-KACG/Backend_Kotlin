package com.back.global.enums

import com.back.global.exception.ServiceException

enum class ClubMemberRole(val description: String) {
    PARTICIPANT("일반 회원"),
    MANAGER("관리자"),
    HOST("소유자");

    companion object {
        fun fromString(role: String): ClubMemberRole {
            return entries.find { it.name.equals(role, ignoreCase = true) }
                ?: throw ServiceException(400, "Unknown Member role: $role")
        }
    }
}