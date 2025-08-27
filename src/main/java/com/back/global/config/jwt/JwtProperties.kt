package com.back.global.config.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom")
class JwtProperties(
    val jwt: Jwt,
    val accessToken: AccessToken
) {

    data class Jwt(
        var secretKey: String? = null
    )

    data class AccessToken(
        var expirationSeconds: Long? = null
    )

    fun getExpirationSecondsAsLong(): Long =
        accessToken.expirationSeconds
            ?: throw IllegalStateException("expirationSeconds is null")
}

