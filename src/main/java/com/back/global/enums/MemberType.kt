package com.back.global.enums

import com.back.global.exception.ServiceException

enum class MemberType(val description: String) {
    MEMBER("회원"),
    GUEST("비회원");

    companion object {
        @JvmStatic
        fun fromString(type: String): MemberType {
            val key = type.trim()
            return values().find { it.name.equals(key, ignoreCase = true) }
                ?: throw ServiceException(400, "Unknown Member type: $type")
        }
    }
}