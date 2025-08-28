package com.back.domain.preset.preset.entity

import com.back.domain.member.member.entity.Member
import jakarta.persistence.*

/**
 * 중요: 이 엔티티 클래스는 JPA가 기본 생성자를 만들 수 있도록
 * build.gradle.kts에 'kotlin-jpa' 플러그인이 설정되어야 합니다.
 */
@Entity
class Preset(
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var owner: Member,

    presetItems: List<PresetItem> = emptyList()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "preset")
    val presetItems: MutableList<PresetItem> = mutableListOf()

    init {
        // 생성 시점에 presetItems를 초기화하고, 양방향 연관관계를 설정합니다.
        presetItems.forEach { addPresetItem(it) }
    }

    fun updateName(name: String) {
        this.name = name
    }

    fun updatePresetItems(newPresetItems: List<PresetItem>) {
        // 기존 아이템들을 모두 제거합니다.
        this.presetItems.clear()
        // 새로운 아이템들을 추가하면서 양방향 연관관계를 설정합니다.
        newPresetItems.forEach { addPresetItem(it) }
    }

    // 연관관계 편의 메서드
    private fun addPresetItem(presetItem: PresetItem) {
        this.presetItems.add(presetItem)
        presetItem.preset = this
    }

    // id 기반으로 엔티티의 동등성을 비교합니다.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Preset
        if (id == null) return false // id가 없는 새 엔티티는 다른 것과 같을 수 없습니다.
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 31
    }
}