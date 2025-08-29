package com.back.domain.club.clubMember.entity

import com.back.domain.checkList.itemAssign.entity.ItemAssign
import com.back.domain.club.club.entity.Club
import com.back.domain.member.member.entity.Member
import com.back.global.enums.ClubMemberRole
import com.back.global.enums.ClubMemberState
import jakarta.persistence.*

@Entity
class ClubMember(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val member: Member,

    @Enumerated(EnumType.STRING)
    var role: ClubMemberRole,

    @Enumerated(EnumType.STRING)
    var state: ClubMemberState,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    var club: Club? = null
        internal set

    @OneToMany(mappedBy = "clubMember", cascade = [CascadeType.ALL], orphanRemoval = true)
    val itemAssigns: MutableList<ItemAssign> = mutableListOf()

    // --- 편의 메서드 ---
    fun addItemAssign(itemAssign: ItemAssign) {
        itemAssigns.add(itemAssign)
        // ItemAssign 클래스에 clubMember 프로퍼티가 var로 선언되어 있다고 가정합니다.
        itemAssign.setClubMember(this)
    }

    fun updateState(newState: ClubMemberState) {
        this.state = newState
    }

    fun updateRole(newRole: ClubMemberRole) {
        this.role = newRole
    }

    // --- equals & hashCode (ID 기준) ---
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ClubMember
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}