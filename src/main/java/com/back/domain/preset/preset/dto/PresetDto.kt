package com.back.domain.preset.preset.dto

import com.back.domain.preset.preset.entity.Preset

data class PresetDto(
    val id: Long?,
    val name: String,
    val presetItems: List<PresetItemDto>
) {
    /**
     * Preset 엔티티를 DTO로 변환하는 보조 생성자
     */
    constructor(preset: Preset) : this(
        id = preset.id,
        name = preset.name,
        // presetItems 리스트를 PresetItemDto 리스트로 변환
        presetItems = preset.presetItems.map { PresetItemDto(it) }
    )
}