package com.back.domain.schedule.schedule.entity

import com.back.domain.checkList.checkList.entity.CheckList
import com.back.domain.club.club.entity.Club
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Schedule(
    title: String,
    content: String,
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    spot: String,
    club: Club,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var title: String = title
        private set

    var content: String = content
        private set

    var startDate: LocalDateTime = startDate
        private set

    var endDate: LocalDateTime = endDate
        private set

    var spot: String = spot //TODO : 나중에 지도 연동하면 좌표로 변경
        private set

    var isActive: Boolean = true
        private set

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var club: Club = club
        private set

    @OneToOne(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnore
    var checkList: CheckList? = null
        private set

    init {
        club.addClubSchedule(this)
    }

    fun updateCheckList(checkList: CheckList?) {
        this.checkList = checkList
    }

    // 일정 수정
    fun modify(title: String, content: String, startDate: LocalDateTime, endDate: LocalDateTime, spot: String) {
        this.title = title
        this.content = content
        this.startDate = startDate
        this.endDate = endDate
        this.spot = spot
    }

    // 일정 비활성화
    fun deactivate() {
        isActive = false
    }

    // 일정 db 삭제 가능 여부
    fun canDelete(): Boolean {
        return checkList == null || checkList?.isActive == false
    }

    // id 기준 동등성 비교
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Schedule

        if (id == 0L) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
