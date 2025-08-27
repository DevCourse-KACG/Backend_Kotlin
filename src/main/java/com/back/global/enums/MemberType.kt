package com.back.global.enums

import com.back.global.exception.ServiceException

enum class MemberType(val description: String) {
    MEMBER("회원"),
    GUEST("비회원");

    companion object {
        fun fromString(type: String): MemberType {
            return entries.find { it.name.equals(type, ignoreCase = true) }
                ?: throw ServiceException(400, "Unknown Member type: $type")
        }
    }
}