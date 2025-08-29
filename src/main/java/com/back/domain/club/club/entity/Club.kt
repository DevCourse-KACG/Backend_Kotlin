package com.back.domain.club.club.entity

import com.back.domain.club.clubMember.entity.ClubMember
import com.back.domain.schedule.schedule.entity.Schedule
import com.back.global.enums.ClubCategory
import com.back.global.enums.EventType
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Club(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 50, nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var bio: String? = null,

    @Column(length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    var category: ClubCategory,

    @Column(length = 256, nullable = false)
    var mainSpot: String,

    @Column(nullable = false)
    var maximumCapacity: Int,

    @Column(nullable = false)
    var recruitingStatus: Boolean = true,

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    var eventType: EventType,

    var startDate: LocalDate? = null,
    var endDate: LocalDate? = null,

    @Column(length = 256)
    var imageUrl: String? = null,

    @Column(nullable = false)
    var isPublic: Boolean = true,

    var leaderId: Long? = null,
    @Column(nullable = false)
    var state: Boolean = true
) {

    @OneToMany(mappedBy = "club", cascade = [CascadeType.ALL], orphanRemoval = true)
    val clubMembers: MutableList<ClubMember> = mutableListOf()

    @OneToMany(mappedBy = "club", cascade = [CascadeType.ALL], orphanRemoval = true)
    val clubSchedules: MutableList<Schedule> = mutableListOf()

    // ---------------- 메서드 ----------------
    fun changeState(state: Boolean) {
        this.state = state
    }

    fun changeRecruitingStatus(recruitingStatus: Boolean) {
        this.recruitingStatus = recruitingStatus
    }

    fun addClubMember(clubMember: ClubMember) {
        clubMembers.add(clubMember)
        clubMember.club = this
    }

    fun removeClubMember(clubMember: ClubMember) {
        clubMembers.remove(clubMember)
        clubMember.club = null
    }

    fun addClubSchedule(schedule: Schedule) {
        clubSchedules.add(schedule)
    }

    fun updateImageUrl(imageUrl: String?) {
        this.imageUrl = imageUrl
    }

    fun updateInfo(
        name: String,
        bio: String?,
        category: ClubCategory,
        mainSpot: String,
        maximumCapacity: Int,
        recruitingStatus: Boolean,
        eventType: EventType,
        startDate: LocalDate?,
        endDate: LocalDate?,
        isPublic: Boolean
    ) {
        this.name = name
        this.bio = bio
        this.category = category
        this.mainSpot = mainSpot
        this.maximumCapacity = maximumCapacity
        this.recruitingStatus = recruitingStatus
        this.eventType = eventType
        this.startDate = startDate
        this.endDate = endDate
        this.isPublic = isPublic
    }

    // ---------------- equals & hashCode (ID 기준) ----------------
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Club) return false
        if (id == null || other.id == null) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
