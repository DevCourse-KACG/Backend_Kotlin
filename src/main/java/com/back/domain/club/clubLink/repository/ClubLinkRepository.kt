package com.back.domain.club.clubLink.repository

import com.back.domain.club.club.entity.Club
import com.back.domain.club.clubLink.entity.ClubLink
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface ClubLinkRepository : CrudRepository<ClubLink, Int> {
    override fun findAll(): List<ClubLink>

    fun findByClubAndExpiresAtAfter(club: Club, expiresAt: LocalDateTime): Optional<ClubLink>

    fun findByInviteCode(inviteCode: String): Optional<ClubLink>
}
