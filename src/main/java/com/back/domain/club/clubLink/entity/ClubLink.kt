package com.back.domain.club.clubLink.entity

import com.back.domain.club.club.entity.Club
import jakarta.persistence.*
import jdk.jfr.Description
import lombok.AllArgsConstructor
import lombok.EqualsAndHashCode
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Entity
@NoArgsConstructor // lombok 없이 직접 작성 필요
@AllArgsConstructor // 마찬가지
@Getter // 불필요, data class 또는 val/var 로 대체
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // 필요시 override
class ClubLink(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    val id: Long? = null,

    @Description("초대 코드")
    @Column(unique = true, nullable = false, length = 50)
    val inviteCode: String,

    @Description("링크 생성 날짜")
    @Column(columnDefinition = "TIMESTAMP")
    val createdAt: LocalDateTime,

    @Description("링크 만료 날짜")
    @Column(columnDefinition = "TIMESTAMP")
    val expiresAt: LocalDateTime,

    @Description("클럽 정보")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    val club: Club
) {

    fun isExpired(): Boolean {
        return expiresAt.isBefore(LocalDateTime.now())
    }

    companion object {
        fun builder(
            inviteCode: String,
            createdAt: LocalDateTime,
            expiresAt: LocalDateTime,
            club: Club
        ): ClubLink {
            return ClubLink(
                inviteCode = inviteCode,
                createdAt = createdAt,
                expiresAt = expiresAt,
                club = club
            )
        }
    }
}
