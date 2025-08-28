package com.back.domain.preset.preset.entity

import com.back.global.enums.CheckListItemCategory
import jakarta.persistence.*

/**
 * 중요: 이 엔티티 클래스는 JPA가 기본 생성자를 만들 수 있도록
 * build.gradle.kts에 'kotlin-jpa' 플러그인이 설정되어야 합니다.
 */
@Entity
class PresetItem(
    @Column(nullable = false)
    val content: String, // 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: CheckListItemCategory, // 카테고리

    @Column(nullable = false)
    val sequence: Int, // 정렬 순서
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set // 외부에서는 id를 변경할 수 없도록 private set으로 설정

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    lateinit var preset: Preset // 프리셋 (양방향 연관관계)

    // id 기반으로 엔티티의 동등성을 비교합니다.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PresetItem
        if (id == null) return false // id가 없는 새 엔티티는 다른 것과 같을 수 없습니다.
        return id == other.id
    }

    // id 기반으로 해시코드를 생성합니다.
    override fun hashCode(): Int {
        return id?.hashCode() ?: 31
    }
}